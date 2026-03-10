[Setup]
AppName=Facturo
AppVersion=1.0
AppPublisher=Facturo
DefaultDirName={pf}\Facturo
DefaultGroupName=Facturo
OutputDir=target\installer
OutputBaseFilename=FacturoSetup
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
SetupIconFile=src\main\resources\com\facturo\facturo.ico

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "target\Facturo.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "target\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Facturo"; Filename: "{app}\Facturo.exe"
Name: "{commondesktop}\Facturo"; Filename: "{app}\Facturo.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\Facturo.exe"; Description: "{cm:LaunchProgram,Facturo}"; Flags: nowait postinstall skipifsilent
