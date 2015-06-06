/*******************************************************************************
  MPLAB Harmony Project Main Source File

  Company:
    Microchip Technology Inc.
  
  File Name:
    main.c

  Summary:
    This file contains the "main" function for an MPLAB Harmony project.

  Description:
    This file contains the "main" function for an MPLAB Harmony project.  The
    "main" function calls the "SYS_Initialize" function to initialize the state 
    machines of all MPLAB Harmony modules in the system and it calls the 
    "SYS_Tasks" function from within a system-wide "super" loop to maintain 
    their correct operation. These two functions are implemented in 
    configuration-specific files (usually "system_init.c" and "system_tasks.c")
    in a configuration-specific folder under the "src/system_config" folder 
    within this project's top-level folder.  An MPLAB Harmony project may have
    more than one configuration, each contained within it's own folder under
    the "system_config" folder.
 *******************************************************************************/

// DOM-IGNORE-BEGIN
/*******************************************************************************
Copyright (c) 2013-2014 released Microchip Technology Inc.  All rights reserved.

//Microchip licenses to you the right to use, modify, copy and distribute
Software only when embedded on a Microchip microcontroller or digital signal
controller that is integrated into your product or third party product
(pursuant to the sublicense terms in the accompanying license agreement).

You should refer to the license agreement accompanying this Software for
additional information regarding your rights and obligations.

SOFTWARE AND DOCUMENTATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF
MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
IN NO EVENT SHALL MICROCHIP OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER
CONTRACT, NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR
OTHER LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES
INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE OR
CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT OF
SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY THIRD PARTIES
(INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 *******************************************************************************/
// DOM-IGNORE-END


// *****************************************************************************
// *****************************************************************************
// Section: Included Files
// *****************************************************************************
// *****************************************************************************

#include <stddef.h>                     // Defines NULL
#include <stdbool.h>                    // Defines true
#include <stdlib.h>                     // Defines EXIT_FAILURE
#include "system/common/sys_module.h"   // SYS function prototypes
#include<xc.h>                      // processor SFR definitions Special Function registers
#include<sys/attribs.h>             // __ISR macro

// *****************************************************************************
// *****************************************************************************
// Section: Main Entry Point
// *****************************************************************************
// *****************************************************************************

int main ( void )
{
    /* Initialize all MPLAB Harmony modules, including application(s). */
    SYS_Initialize ( NULL );
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

    // set up B4 and B5 pins as a digital output
    TRISBbits.TRISB4 = 0;  // pin B4 output
    TRISBbits.TRISB5 = 0;  // pin B5 output
    LATBbits.LATB5 = 1;    // turn on the Motor B
    LATBbits.LATB4 = 1;    // turn on the Motor B
    //LATBbits.LATB5 = 1;    // turn on the Motor B
    //LATBbits.LATB5 = 0;    // turn on the Motor A
    
    // set up OC3 using Timer2 at 20KHz
    ANSELBbits.ANSB14 = 0;  // disable the analog function
    RPB14Rbits.RPB14R = 0b0101; // Set RB15 is OC1
    T2CONbits.TCKPS = 0;   // Timer2 prescaler N=1  PLL 40M Hz = 25 ns, OC1 20kHz = 50us
    // ( PR2 + 1 ) * prescaler = 50us / 25ns = 2000   PR2 + 1 = 2000 / 1 = 2000
    PR2 = 1999;
    TMR2 = 0; // initial TMR2 count is 0
    OC3CONbits.OCM = 0b110; // PWM mode without fault pin; other OC1CON bits are defaults
    OC3RS = 0; // duty cycle = OC1RS/(PR2+1) = 50%
    OC3R = 0; // initialize before turning OC1 on; afterward it is read-only
    T2CONbits.ON = 1; // turn on Timer2
    OC3CONbits.ON = 1; // turn on OC1
    
    
    // set up OC3 using Timer3 at 20KHz
    ANSELBbits.ANSB15 = 0;  // disable the analog function
    RPB15Rbits.RPB15R = 0b0101; // Set RB13 is OC4
    T4CONbits.TCKPS = 0;   // Timer3 prescaler N=1  PLL 40M Hz = 25 ns, OC1 20kHz = 50us
    // ( PR3 + 1 ) * prescaler = 50us / 25ns = 2000   PR3 + 1 = 2000 / 1 = 2000
    PR4 = 1999;
    TMR4 = 0; // initial TMR3 count is 0
    OC1CONbits.OCM = 0b110; // PWM mode without fault pin; other OC1CON bits are defaults
    OC1RS = 2000; // duty cycle = OC3RS/(PR3+1) = 50%
    OC1R = 2000; // initialize before turning OC3 on; afterward it is read-only
    T4CONbits.ON = 1; // turn on Timer3
    OC1CONbits.ON = 1; // turn on OC3
    
    __builtin_enable_interrupts();
    
    

    while ( true )
    {
        /* Maintain state machines of all polled MPLAB Harmony modules. */
        SYS_Tasks ( );

    }

    /* Execution should not come here during normal operation */

    return ( EXIT_FAILURE );
}


/*******************************************************************************
 End of File
*/

