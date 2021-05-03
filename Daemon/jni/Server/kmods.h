#ifndef KMODS_H
#define KMODS_H

#include <cstdio>
#include <cstdlib>
#include <cstddef>
#include <dirent.h>
#include <unistd.h>
#include <cmath>
#include <ctime>
#include <string>
#include <vector>
#include <list>
#include <map>

#include "Log.h"
#include "Process.h"
#include "Memory.h"
#include "Offsets.h"

static const char* lib_name = "libUE4.so";

int Width = 1920;
int Height = 1080;

bool isStartWith(string str, string check) {
	return (str.rfind(check, 0) == 0);
}

bool isEqual(string s1, string s2) {
	return (s1 == s2);
}

bool isContain(string str, string check) {
	size_t found = str.find(check);
	return (found != string::npos);
}

#endif
