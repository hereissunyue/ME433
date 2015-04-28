#ifdef WIN32
#include <windows.h>
#endif
#include <stdio.h>
#include <stdlib.h>
#include "hidapi.h"

#define MAX_STR 255

int main(int argc, char* argv[])
{
	int res, a;
	unsigned char buf[65] = {0};
	unsigned char string[25] = {0}, row;
	wchar_t wstr[MAX_STR];
	unsigned short length;
	hid_device *handle;
	int i,j=0,k=1;
	FILE *fp = fopen("accdata.txt", "ab+");

	// Initialize the hidapi library
	res = hid_init();

	// Open the device using the VID, PID,
	// and optionally the Serial number.
	handle = hid_open(0x4d8, 0x3f, NULL);

	// Read the Manufacturer String
	res = hid_get_manufacturer_string(handle, wstr, MAX_STR);
	wprintf(L"Manufacturer String: %s\n", wstr);

	// Read the Product String
	res = hid_get_product_string(handle, wstr, MAX_STR);
	wprintf(L"Product String: %s\n", wstr);

	// Read the Serial Number String
	res = hid_get_serial_number_string(handle, wstr, MAX_STR);
	wprintf(L"Serial Number String: (%d) %s\n", wstr[0], wstr);

	// Read Indexed String 1
	res = hid_get_indexed_string(handle, 1, wstr, MAX_STR);
	wprintf(L"Indexed String 1: %s\n", wstr);

	printf("Please enter the row number: ");
	scanf("%d", &row);
	getchar(); // REQUIRED HERE!!!!!
	printf("Please enter the string: ");
	scanf("%[^\n]s", string);
	getchar(); // REQUIRED HERE!!!!!
	// Toggle LED (cmd 0x80). The first byte is the report number (0x0).
	buf[0] = 0x0;
	buf[1] = 0x80;
	buf[2] = row;
	// save the string to buf
	j = 0;
	while (string[j])
	{
		buf[j + 3] = string[j];
		j = j + 1;
	}

	res = hid_write(handle, buf, 65);

	// Request state (cmd 0x81). The first byte is the report number (0x0).
	//buf[0] = 0x0;
	//buf[1] = 0x81;
	//res = hid_write(handle, buf, 65);

	// Read requested state
	printf("Please enter data length: ");
	scanf("%d", &length);
	getchar(); // REQUIRED HERE!!!!!

	fp = fopen("accdata.txt", "w");
	if (fp == NULL)
		exit(-1);
	fprintf(fp, "#x    y    z\n");

	// k is changeable but is k is too big corresponding j should be changed in app.c line 365
	while (k <= length)
	{
		res = hid_read(handle, buf, 65);
		//printf("x: %3.1f g\n", ((2 * buf[0] - 1)*(buf[1])*1.0) / 10);
		//printf("y: %3.1f g\n", ((2 * buf[2] - 1)*(buf[3])*1.0) / 10);
		//printf("z: %3.1f g\n\n", ((2 * buf[4] - 1)*(buf[5])*1.0) / 10);
		k = k + 1;
		fprintf(fp, "%3.1f  %3.1f  %3.1f\n", ((2 * buf[0] - 1)*(buf[1])*1.0) / 10, ((2 * buf[2] - 1)*(buf[3])*1.0) / 10, ((2 * buf[4] - 1)*(buf[5])*1.0) / 10);
	}
	
	fclose(fp);

	printf("\nData Acquisition Successfully Done!\nData Length %d!\n", length);
	// Finalize the hidapi library
	res = hid_exit();

	return 0;
}