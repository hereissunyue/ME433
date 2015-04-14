ME433 Advanced Mechatronics
---------------------------------
<br> 
Assignment 4  
---------------------------------

[Source Code and Library](https://github.com/hereissunyue/ME433/tree/master/HW4/HW4.X)<br> 
<b>Code for displaying message is in the i2c_display.c as following</b>  

```bash
void display_message(char message[], char x0, char y0)
{
    unsigned char i = 0, j, k, status, changeline = x0;    // start from the first character
    unsigned char y = y0, x = x0, hexvalue;  // start LED Point
	while (message[i])
	{
        // index all five columns of message[i] and set the OLED
        if(y >= 125){y = y0; changeline = changeline + 8;} // reset the x column location and new row
        for (j = 0; j <= 4; j++)
        {
            hexvalue = ASCII[message[i] - 32][j];  // read the hex value out of matrix   
            if(x>=58){break;}
            x = changeline;
            // calculate the reminder to assign the status of the pixel
            for (k = 1; k <= 7; k++)
            {
                status = hexvalue % 2;
                hexvalue = hexvalue / 2;
                x = x + 1; // for the same column starts from the second pixel
                display_pixel_set(x, y, status); // set with the reminder value
            }
            y = y + 1;  // column increase 1 
        }
		i++;
	}
}
```

1. Display "Hello world 1337!" starting at (28,32)
---------------------------------
<img src="https://raw.githubusercontent.com/hereissunyue/ME433/master/HW4/figure/IMAG1011.jpg">
<br> 
<b>I intentionally add a pixel (27,31) for location reference!</b>



2. How many characters can be displayed on the screen at the same time?
---------------------------------
Since the screen is 128x64 pixels and each character occupies 5x8 pixels, the screen could totally display 25x8=200 characters.