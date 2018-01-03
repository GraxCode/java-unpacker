## What is this project about?
The ReverseCrypter can extract jar archives crypted by various java-crypters. The extractors (and their keys!) are hard-coded and may not work for newer versions.

## Supported Crypters
| Crypter | Extractor |
| --- | --- |
| CoreProject by ??? | CoreProtectEx |
| JCrypt 1.5 by redpois0n | JCryptEx |
| JarProtector 1.1.1 by Burn3diC3 | JarProtectorEx |

## CLI
| Argument | Description |
| --- | --- |
| --help | Displays help |
| --input | Specify input file |
| --output | Specify output file |
| --extractor | Specify extractor |

## Libraries needed
commons-io 2.6, commons-cli 1.4

## License
ReverseCrypt is licensed under the GNU General Public License