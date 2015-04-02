/*
 * File:   hw1.c
 * Author: Yue Sun
 *
 * Created on April 1, 2015, 11:08 AM
 */

#include<xc.h>                      // processor SFR definitions Special Function registers
#include<sys/attribs.h>             // __ISR macro


// DEVCFG0
#pragma config DEBUG = OFF // Background Debugger disabled
#pragma config JTAGEN = OFF // no jtag
#pragma config ICESEL = ICS_PGx1 // use PGED1 and PGEC1
#pragma config PWP = OFF // no write protect
#pragma config BWP = OFF // not boot write protect
#pragma config CP = OFF // no code protect

// DEVCFG1
#pragma config FNOSC = PRIPLL // Oscillator Selection: Primary oscillator w/ PLL
#pragma config FSOSCEN = OFF // Disable second osc to get pins back
#pragma config IESO = OFF // no switching clocks
#pragma config POSCMOD = HS // Primary Oscillator Mode: High Speed xtal
#pragma config OSCIOFNC = OFF // free up secondary osc pins
#pragma config FPBDIV = DIV_1 // Peripheral Bus Clock: Divide by 1
#pragma config FCKSM = CSDCMD // do not enable clock switch
#pragma config WDTPS = PS1048576 // slowest wdt
#pragma config WINDIS = OFF // no wdt window
#pragma config FWDTEN = OFF // wdt off by default
#pragma config FWDTWINSZ = WINSZ_25 // wdt window at 25%

// DEVCFG2 - get the CPU clock to 40MHz
#pragma config FPLLIDIV = DIV_2 // divide input clock to be in range 4-5MHz  8M / 2 = 4M
#pragma config FPLLMUL = MUL_20 // multiply clock after FPLLIDIV  4M * 20 = 80M  PLL Multiplier: Multiply by 20
#pragma config UPLLIDIV = DIV_2 // USB clock  8M / 2 = 4M
#pragma config UPLLEN = ON // USB clock on
#pragma config FPLLODIV = DIV_2 // divide clock by 2 to output on pin 80M / 2 = 40M

// DEVCFG3
#pragma config USERID = 0 // some 16bit userid
#pragma config PMDL1WAY = ON // not multiple reconfiguration, check this
#pragma config IOL1WAY = ON // not multimple reconfiguration, check this
#pragma config FUSBIDIO = ON // USB pins controlled by USB module
#pragma config FVBUSONIO = ON // controlled by USB module

//	Function Prototypes
int readADC(void);


// Timer1 ISR function Frequency: 10kHz   Priority: Level 7
void __ISR(_TIMER_1_VECTOR, IPL7SOFT) LEDControl()
{ // _TIMER_1_VECTOR = 4 (p32mx250f128b.h)
    unsigned int ADCval;
    ADCval = readADC(); // read the ADC value
    OC1RS =  40000*ADCval / 1024;   // duty cycle proportional to the potentiometer voltage  
    IFS0bits.T1IF = 0;              // clear interrupt flag
}


int main(void) 
{
    //Startup code to run as fast as possible and get pins back from bad defaults
    __builtin_disable_interrupts();
    
    // set the CP0 CONFIG register to indicate that
    // kseg0 is cacheable (0x3) or uncacheable (0x2)
    // see Chapter 2 "CPU for Devices with M4K Core"
    // of the PIC32 reference manual
    // no cache on this chip!
    __builtin_mtc0(_CP0_CONFIG, _CP0_CONFIG_SELECT, 0xa4210582);
    // 0 data RAM access wait states
    BMXCONbits.BMXWSDRM = 0x0;
    // enable multi vector interrupts
    INTCONbits.MVEC = 0x1;
    // disable JTAG to be able to use TDI, TDO, TCK, TMS as digital
    DDPCONbits.JTAGEN = 0;
    
    // set up USER pin as input Pin B13
    ANSELBbits.ANSB13 = 0;  // disable the analog function
    TRISBbits.TRISB13 = 1;  // pin B13 input

    // set up LED1 pin as a digital output Pin B7
    TRISBbits.TRISB7 = 0;  // pin B7 output
    LATBbits.LATB7 = 1;    // turn on the LED1
    
    // set up LED2 as OC1 using Timer2 at 1kHz
    RPB15Rbits.RPB15R = 0b0101; // Set RB15 is OC1
    T2CONbits.TCKPS = 0;   // Timer2 prescaler N=1  PLL 40M Hz = 25 ns, OC1 1kHz = 1ms
    // ( PR2 + 1 ) * prescaler = 1ms / 25ns = 40000   PR2 + 1 = 40000 / 1 = 40000
    PR2 = 39999;
    TMR2 = 0; // initial TMR2 count is 0
    OC1CONbits.OCM = 0b110; // PWM mode without fault pin; other OC1CON bits are defaults
    OC1RS = 20000; // duty cycle = OC1RS/(PR2+1) = 50%
    OC1R = 20000; // initialize before turning OC1 on; afterward it is read-only
    T2CONbits.ON = 1; // turn on Timer2
    OC1CONbits.ON = 1; // turn on OC1
    
    // set up A0 as AN0
    ANSELAbits.ANSA0 = 1;
    AD1CON3bits.ADCS = 3;
    AD1CHSbits.CH0SA = 0;
    AD1CON1bits.ADON = 1;
    
    // Set up timer1 for ISR
    T1CONbits.TCKPS = 0b10;         // set prescaler to 16
    T1CONbits.TGATE = 0;            // not gated input (the default)
    T1CONbits.TCS = 0;              // PCBLK input (the default)
    PR1 = 250;						// (PR1 + 1)* precaler1 = 40M/10K = 4000  with precaler1 = 16  PR1 = 25
    TMR1 = 0;                       // initialize count to 0
    T1CONbits.ON = 1;               // turn on Timer1
    // Setting Interrupt for Timer 1
    IPC1bits.T1IP = 7;              // INT step 4: priority 7
    IPC1bits.T1IS = 0;              //             subpriority 0
    IFS0bits.T1IF = 0;              // INT step 5: clear interrupt flag
    IEC0bits.T1IE = 1;              // INT step 6: enable interrupt
    
    __builtin_enable_interrupts();
    
    
    // Main while loop
    while (1) 
    {
        _CP0_SET_COUNT(0); // Reset the core counter
        while(_CP0_GET_COUNT() < 10000000) 
		{ if (PORTBbits.RB13 == 0){ break; } } // once the button is triggered the loop break
        
        if (PORTBbits.RB13 == 1){ LATBINV = 0x0080; } // inverse the LED1
        else{ LATBbits.LATB7 = 1; } // turn on the LED1
    }
}

// ADC value reading
int readADC(void) 
{
    int elapsed = 0;
    int finishtime = 0;
    int sampletime = 20;
    int a = 0;

    AD1CON1bits.SAMP = 1;
    elapsed = _CP0_GET_COUNT();
    finishtime = elapsed + sampletime;
    while (_CP0_GET_COUNT() < finishtime) {;}
    AD1CON1bits.SAMP = 0;
	while (!AD1CON1bits.DONE) {;}
    a = ADC1BUF0;
    return a;
}

