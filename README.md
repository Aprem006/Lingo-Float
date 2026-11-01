name: Build APK

on:
  push:
  workflow_dispatch:

permissions:
  contents: write

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - uses: actions/setup-python@v5
        with:
          python-version: '3.12'

      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.7'

      - name: Unpack project
        run: unzip -o project.zip

      - name: Make app icon from logo
        run: |
          pip install pillow
          python make_icons.py

      - name: Build
        run: gradle assembleDebug --stacktrace

      - name: Rename APK
        run: cp app/build/outputs/apk/debug/app-debug.apk LingoFloat.apk

      - name: Publish APK
        uses: softprops/action-gh-release@v2
        with:
          tag_name: lingofloat-${{ github.run_number }}
          name: LingoFloat v${{ github.run_number }}
          body: "Download LingoFloat.apk below and install it. Indonesian to English on your whole screen."
          files: LingoFloat.apk
          
