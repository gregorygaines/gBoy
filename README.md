# gBoy

Gameboy emulator written in native Java, built using maven. The goal was
to test my reverse engineering skills and programming abilities to see
if I can preform one of the hardest software engineering task ever, make
a computer system in PURE CODE.

![gBoy Running](docs/gb1.PNG)

# Building and running
The emulator is built with the maven command:
```
mvn clean compile package
```
The runnable jar will be in the directory /target

# Features
- Full GMB emulation
- Fully implemented Z80 instruction set
- Fully implemented GMB LCD
- Cartridge mappers MBC1, MBC3, and MBC5
- Semi implemented serial cable
- GUI and file opener

# Todo
- Implement sound
- Check CPU cycle accuracy
- Implement all cartridge save mappers
- Fix game edge cases
- Implement CGB

# Screenshots
![gBoy Running](docs/gb2.PNG)
![gBoy Running](docs/gb3.PNG)
![gBoy Running](docs/gb5.PNG)
![gBoy Running](docs/gb6.PNG)
![gBoy Running](docs/gb7.PNG)
![gBoy Running](docs/gb8.PNG)
![gBoy Running](docs/gb9.PNG)

# Resources
- Z80 Documents
- http://pastraiser.com/cpu/gameboy/gameboy_opcodes.html - GameBoy Opcode Documentation
- http://www.myquest.nl/z80undocumented/z80cpu_um.pdf - Z80 Documentation
- http://gameboy.mongenel.com/dmg/opcodes.html - GameBoy Opcode Documentation
- https://gbdev.gg8.se/files/roms/blargg-gb-tests/ - Blargg Test Roms
- http://bgb.bircd.org/pandocs.htm - GameBoy Documents
- http://www.codeslinger.co.uk - GameBoy Emulator Tutorial
- http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf - GameBoy Documentation