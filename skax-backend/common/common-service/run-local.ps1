[Console]::InputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 > $null

./gradlew.bat --no-daemon --console=plain clean bootRun --args="--spring.profiles.active=local"
