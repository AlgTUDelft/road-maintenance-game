Attribute VB_Name = "modExport"
Option Explicit

' Exports the maintenance task lists
Public Sub ExportTasks(OutFile As String, sheet As Worksheet, startRow As Integer)
    Open OutFile For Output As #1
    
    ' get worksheet
    Dim ws As Worksheet
    Set ws = sheet
            
    Dim row As Integer
    row = startRow
    
    Print #1, "<Tasks>"
    
    ' keep track of task number so we know when to print task info (once)
    Dim LastTaskNo As Integer
    LastTaskNo = -1
    
    ' go through all methods
    While ws.Cells(row, 1) <> ""
        ' read all required info
        ' basic method info
        Dim TaskNo As Integer, Segment As String, Desc As String, MethodNo As Integer, LongDesc As String
        TaskNo = CInt(ws.Cells(row, 1))
        Segment = getAsset(ws.Cells(row, 2))
        MethodNo = CInt(ws.Cells(row, 3))
        Desc = ws.Cells(row, 4)
        LongDesc = ws.Cells(row, 5)
        
        ' duration info
        Dim Duration As Integer, DelayDuration As Integer, DelayProb As Double
        Duration = CInt(ws.Cells(row, 9))
        DelayDuration = CInt(ws.Cells(row, 12))
        DelayProb = 0.166667
        
        ' cost info
        Dim Revenue As Double, Cost As Double
        Revenue = CDbl(ws.Cells(row, 14))
        Cost = CDbl(ws.Cells(row, 8))
        
        ' TTL impact
        Dim TTLClass As String, MethodTTLClass As String
        TTLClass = toDec(CDbl(ws.Cells(row, 15).Value), 2) & " (" & getTTLClass(CInt(ws.Cells(row, 15).Value)) & ")"
        MethodTTLClass = toDec(CDbl(ws.Cells(row, 16).Value) - CDbl(ws.Cells(row, 15).Value), 2)
        
        ' quality demand/impact
        Dim QualityDemand As Double, QualityEffect As Double
        QualityDemand = 6  ' FIXME: CInt(ws.Cells(row, ))
        QualityEffect = CDbl(ws.Cells(row, 11))
        
        ' print task info?
        If LastTaskNo <> TaskNo Then
            LastTaskNo = TaskNo
            
            Dim TaskID As String
            TaskID = "T" & TaskNo
            
            Print #1, "  <Task ID=""" & TaskID & """ Desc=""" & Segment & " (" & TaskID & ")"" AssetID=""" & Segment & """ LongDesc=""" & LongDesc & """ Payment=""" & toDec4(Revenue) & """ TTLClass=""" & TTLClass & """ QualityDemand=""" & toDec4(QualityDemand) & """ >"
        End If
        
        ' print method info
        Print #1, "    <Method ID=""M" & MethodNo & """ Desc=""" & Desc & """ CostFunction=""Constant:" & toDec4(Cost / Duration) & """ TTLImpact=""" & MethodTTLClass & """ QualityImpact=""" & toDec4(QualityEffect) & """ Duration=""" & Duration & "w"" DelayDuration=""" & DelayDuration & "w"" DelayRisk=""" & toDec4(DelayProb) & """ />"
        
        row = row + 1
        
        ' check if we should close the task tag
        If ws.Cells(row, 1) <> ws.Cells(row - 1, 1) Then Print #1, "  </Task>"
    Wend
    
    Print #1, "</Tasks>"
            
    Close #1
End Sub


' Exports all TTL info to the specified file
Public Sub ExportIdleTTL(OutFile As String, sheet As Worksheet, startCol As Integer, startRow As Integer)
    Dim ws As Worksheet
    Set ws = sheet
    
    'On Error GoTo fileerr
    Close
    Open OutFile For Output As #1
    
    Print #1, "<AssetRules Type=""Function"">"
    
        ' go through all rows
        Dim i As Integer
        i = startRow
        While ws.Cells(i, 1).Value <> ""
            ' export the current row
            Print #1, vbTab & "<Rule Asset=""" & getAsset(ws.Cells(i, 1).Value) & Chr(34);
            Print #1, " Function=""Table:";
            
            ' export all table values
            Dim j As Integer
            j = startCol
            While ws.Cells(i, j).Value <> ""
                Print #1, toDec4(CDbl(ws.Cells(i, j).Value)) & ";";
                j = j + 1
            Wend
            
            ' and close the tag
            Print #1, """ />"
            
            i = i + 1
        Wend
    
    Print #1, "</AssetRules>"
    
    Close #1
Exit Sub
fileerr:
    MsgBox Err.Description
    Close
End Sub

' Exports the network TTL matrix
Public Sub ExportNetworkTTLMatrix(OutFile As String, sheet As Worksheet, startCol As Integer, startRow As Integer)
    Open OutFile For Output As #1
    
    Dim ws As Worksheet
    Set ws = sheet
        
    ' start from first factor row
    Dim row As Integer
    row = startRow
    
    Print #1, "<NetworkRules Type=""ImpactFactor"">"
            
    While ws.Cells(row, 1).Value <> ""
        ' print rule tag
        Dim T As Integer, M As Integer, first As Boolean
        T = CInt(ws.Cells(row, 1))
        M = CInt(ws.Cells(row, 3))
        first = True
        Print #1, vbTab & "<Rule Method=""T" & T & ".M" & M & """ Impact=""";
    
        ' get factors
        Dim col As Integer
        col = startCol
        While ws.Cells(row, col) <> ""
            Dim f As Double
            If Left(ws.Cells(row, col), 1) <> "#" Then
                f = CDbl(ws.Cells(row, col))
            Else
                f = 1
            End If
                
            If f <> 1 Then
                ' print the factor for the segment
                Print #1, IIf(first, "", ";") & getAsset(ws.Cells(1, col)) & "|" & toDec4(ws.Cells(row, col));
                first = False
            End If
            col = col + 1
        Wend
        Print #1, """ />"
        
        ' next row
        row = row + 1
    Wend
    
    Print #1, "</NetworkRules>"
    
    Close #1
End Sub

' Converts the A80-C notation to A80C
Private Function getAsset(asset As String) As String
    getAsset = Left(asset, InStr(asset, "-") - 1) & Mid(asset, InStr(asset, "-") + 1)
End Function

' Converts decimal number to use period as separator
Private Function toDec4(dec As Double) As String
    toDec4 = toDec(dec, 4)
End Function

' Converts decimal number to use period as separator and specified number of digits
Private Function toDec(dec As Double, decimals As Integer) As String
    Dim f As String
    f = "0"
    If decimals > 0 Then
        f = f & "."
        Dim i As Integer
        For i = 0 To decimals - 1
            f = f & "0"
        Next i
    End If
        
    toDec = Replace(Format(dec, f), ",", ".")
End Function

' Returns the TTL class that corresponds to the ttl
Private Function getTTLClass(ttl As Integer) As String
    Select Case ttl
        Case Is < 10:
            getTTLClass = "Low"
        Case Is < 20:
            getTTLClass = "Moderate"
        Case Is < 30:
            getTTLClass = "Heavy"
        Case Else
            getTTLClass = "Extreme"
    End Select
End Function
