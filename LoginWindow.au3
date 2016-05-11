


While 1
WinWaitActive("","Authentication Required","120")
If WinExists("","Authentication Required") Then
	ControlSend
Send("username{TAB}")
Send("password{Enter}")
EndIf

WEnd

