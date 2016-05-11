While 1

WinWait("Authentication Required","",10)
WinActivate("Authentication Required")
if WinExists("Authentication Required") Then
	Send("charlie\pimishra")
	Sleep(1000)
	Send("{TAB}")
	Send("F22@6ASHATABDI")
	Sleep(1000)
	Send("{TAB}")
	Send("{ENTER}")
EndIf

WinWait("Windows Security","",10)
WinActivate("Windows Security")
if WinExists("Windows Security") Then
	Send("charlie\pimishra")
	Sleep(1000)
	Send("{TAB}")
	Send("F22@6ASHATABDI")
	Sleep(1000)
	Send("{TAB}")
	Send("{ENTER}")
EndIf


WinWaitActive("", "Authentication Required", 10)
if WinExists("Authentication Required") Then
	Send("charlie\pimishra")
	Sleep(1000)
	Send("{TAB}")
	Send("F22@6ASHATABDI")
	Sleep(1000)
	Send("{TAB}")
	Send("{ENTER}")
EndIf

WEnd