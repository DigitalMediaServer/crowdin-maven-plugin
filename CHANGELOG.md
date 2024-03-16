# Changelog for crowdin-maven-plugin
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Created this changelog.

## [1.1.2] - 2024-02-14
### Added
- Added option to write Unicode BOM when deploying translation files.
### Fixed
- Fixed syntax in documentation template ([#3](https://github.com/DigitalMediaServer/crowdin-maven-plugin/pull/3)). Thanks to [@SubJunk](https://github.com/SubJunk).
### Changed
- Updated dependencies: 
  - Maven components to the last versions that support Java 7.
  - `org.jdom:jdom2` to `2.0.6.1`.

## [1.1.1] - 2021-08-06
### Fixed
- Minor bugfixes.
### Changed
- Updated dependencies: 
  - `org.apache.httpcomponents:httpclient` to `4.5.13` ([CVE-2020-13956](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-13956)).
  - `com.jcraft:jsch` to `0.1.54` ([CVE-2016-5725](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2016-5725)).
  - `org.apache.maven.plugin-tools:maven-plugin-annotations` to `3.6.1`.
  - `org.apache.maven:maven-core` to `3.8.1`.
  - `org.apache.maven:maven-plugin-api` to `3.8.1`.

## [1.1.0] - 2018-09-30
### Added
- Implemented special handling for NSIS (`*.nsh`) files.
### Changed
- Updated dependency: `org.apache.httpcomponents:httpclient` to `4.5.6`.

## [1.0.1] - 2018-06-29
### Fixed
- Made sure that global parameters were applied.

## [1.0.0] - 2018-06-28
### First release
- Requires Java 7 or later.

[Unreleased]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/compare/v1.1.2...HEAD
[1.1.2]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/DigitalMediaServer/crowdin-maven-plugin/releases/tag/v1.0.0
