ifeq ($(OS), Windows_NT)
	MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew.bat
else
	MAKEFILE_PATH := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew
endif

GRADLE_DEFAULT_PARAMETERS := --console=plain

.DEFAULT_GOAL := clean

.PHONY: clean purge rebuild-build-logic compile build install publish test wrapper count-code-lines

.SILENT:

clean:
	$(GRADLEW) 'clean' "$(GRADLE_DEFAULT_PARAMETERS)"

purge: clean
	$(GRADLEW) ':buildSrc:clean' "$(GRADLE_DEFAULT_PARAMETERS)"
ifeq ($(OS), Windows_NT)
	if exist $(MAKEFILE_PATH)\.gradle rmdir /s /q $(MAKEFILE_PATH)\.gradle
	if exist $(MAKEFILE_PATH)\buildSrc\.gradle rmdir /s /q $(MAKEFILE_PATH)\buildSrc\.gradle
	if exist $(MAKEFILE_PATH)\buildSrc\.kotlin rmdir /s /q $(MAKEFILE_PATH)\buildSrc\.kotlin
else
	rm -rf $(MAKEFILE_PATH)/.gradle
	rm -rf $(MAKEFILE_PATH)/buildSrc/.gradle
	rm -rf $(MAKEFILE_PATH)/buildSrc/.kotlin
endif

rebuild-build-logic:
	$(GRADLEW) ':buildSrc:clean' "$(GRADLE_DEFAULT_PARAMETERS)"
	$(GRADLEW) ':buildSrc:jar' "$(GRADLE_DEFAULT_PARAMETERS)"

compile:
	$(GRADLEW) 'classes' "$(GRADLE_DEFAULT_PARAMETERS)"

build:
	$(GRADLEW) -x "test" "build" "$(GRADLE_DEFAULT_PARAMETERS)"

install:
	$(GRADLEW) -x "test" "publishToMavenLocal" --no-parallel "$(GRADLE_DEFAULT_PARAMETERS)"

publish: install
	echo "警告：即将发布到Maven中央仓库！"
	read -p "确认继续？(yes/no) " confirm && [ $$confirm = "yes" ] || exit 1
	$(GRADLEW) -x "test" "publishAllPublicationsToMavenCentralRepository" --no-parallel "$(GRADLE_DEFAULT_PARAMETERS)"

test:
	$(GRADLEW) "test" "$(GRADLE_DEFAULT_PARAMETERS)"

wrapper:
	$(GRADLEW) ":wrapper" "$(GRADLE_DEFAULT_PARAMETERS)"

count-code-lines:
	$(GRADLEW) "countCodeLines" "$(GRADLE_DEFAULT_PARAMETERS)"
