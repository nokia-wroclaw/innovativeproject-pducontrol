# innovativeproject-pducontrol
Aplikacja mobilna na platfotmy Android i Windows Mobile, służąca do zdalnej kontroli urządzeń PDU  (Power Distribution Unit)

W razie błędu typu: 
Error:Minimum supported Gradle version is 2.14.1, Gradle x.x is not supported yet. Current version is 2.10. If using the gradle wrapper, try editing the distributionUrl in ...\gradle-wrapper.properties to gradle-2.10-all.zip

należy zmienić w settings > build, execution, deployment > gradle zaznaczć use default gradle wrapper

W razie błędu:
Error:Execution failed for task ':app:transformClassesWith
InstantRunForDebug'. > JSR/RET are not supported with computeFrames option

należy wyłączyć opcję settings > build, execution, deployment > instant run "enable instant run"
