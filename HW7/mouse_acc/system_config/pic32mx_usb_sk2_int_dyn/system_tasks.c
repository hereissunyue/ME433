/*******************************************************************************
 System Tasks File

  File Name:
    system_tasks.c

  Summary:
    This file contains source code necessary to maintain system's polled state
    machines.

  Description:
    This file contains source code necessary to maintain system's polled state
    machines.  It implements the "SYS_Tasks" function that calls the individual
    "Tasks" functions for all the MPLAB Harmony modules in the system.

  Remarks:
    This file requires access to the systemObjects global data structure that
    contains the object handles to all MPLAB Harmony module objects executing
    polled in the system.  These handles are passed into the individual module
    "Tasks" functions to identify the instance of the module to maintain.
 *******************************************************************************/

// DOM-IGNORE-BEGIN
/*******************************************************************************
Copyright (c) 2013-2014 released Microchip Technology Inc.  All rights reserved.

Microchip licenses to you the right to use, modify, copy and distribute
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

#include "system_config.h"
#include "system_definitions.h"
#include "app.h"
#include "accel.h"                      // accelerometor library
#include<xc.h>                          // processor SFR definitions Special Function registers
#include<sys/attribs.h>                 // __ISR macro
#include "i2c_master_int.h"             // OLED master library
#include "i2c_display.h"                // OLED display library

// *****************************************************************************
// *****************************************************************************
// Section: System "Tasks" Routine
// *****************************************************************************
// *****************************************************************************

/*******************************************************************************
  Function:
    void SYS_Tasks ( void )

  Remarks:
    See prototype in system/common/sys_module.h.
*/

void SYS_Tasks ( void )
{
    
    short accels[3]; // accelerations for the 3 axes
    char message[10]; 
    signed char x,y;
    //sprintf(message,"Hello world %d!  Yue Sun", number);
 
    
    // read the accelerometer from all three axes
    // the accelerometer and the pic32 are both little endian by default (the lowest address has the LSB)
    // the accelerations are 16-bit twos compliment numbers, the same as a short
    acc_read_register(OUT_X_L_A, (unsigned char *) accels, 6);    
    x = (accels[0] * 9.8)/ 17000;
    if (abs(x)<1){x=0;}
    y = (accels[1] * 9.8)/ 17000;
    if (abs(y)<1){y=0;}    
    
    display_clear();
    sprintf(message,"accx = %3.1f", (accels[0] * 9.8)/ 17000);
    display_message(message,2,2);
    sprintf(message,"accy = %3.1f", (accels[1] * 9.8)/ 17000);
    display_message(message,10,2);
    sprintf(message,"unit: m/s^2");
    display_message(message,18,2);
    display_draw();
        //display the message 
    
    /* Maintain the state machines of all library modules executing polled in
    the system. */

    /* Maintain system services */
    SYS_DEVCON_Tasks(sysObj.sysDevcon);

    /* Maintain Device Drivers */

    /* Maintain USB Stack */
    /* Device layer tasks routine */ 
    USB_DEVICE_Tasks(sysObj.usbDevObject0);
    
    /* Maintain the application's state machine. */
    APP_Tasks(x, y);
}


/*******************************************************************************
 End of File
 */

