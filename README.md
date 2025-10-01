# BTD3D - Bloons Tower Defense 3D

A 3D tower defense game built with Java using LWJGL (Lightweight Java Game Library). Defend against waves of bloons by strategically placing towers along their path.

## Features

- **3D Graphics**: Fully 3D game world with animated models and effects
- **Tower Defense Gameplay**: Classic BTD mechanics with multiple tower types
- **Physics Simulation**: Realistic projectile physics using Bullet Physics
- **Multiple Tower Types**: Dart Monkey, Sniper Monkey, Bomb Tower, Super Monkey
- **Bloon Varieties**: From basic red bloons to tough MOABs and special types (Lead, Ceramic, etc.)
- **Upgrade System**: Two upgrade paths per tower type
- **Audio System**: 3D positional audio with music and sound effects
- **ImGui Interface**: Modern immediate mode GUI for game controls

## Screenshots & Demo

### Gameplay Screenshots

<div align="center">

![Screenshot 1](assets/pictures/ss1.png)
*Early game setup - Placing towers and starting defense*

![Screenshot 2](assets/pictures/ss2.png) 
*Mid-game action - Multiple towers engaging waves of bloons*

![Screenshot 3](assets/pictures/ss3.png)
*Advanced gameplay - Complex bloon types including MOABs*

</div>

### Video Demo
🎥 **Click to watch gameplay video:**

[![BTD3D Gameplay Demo](https://img.youtube.com/vi/os39cU27Hi4/maxresdefault.jpg)](https://youtu.be/os39cU27Hi4)

</div>

## Requirements

- **Java**: JDK 8 or higher
- **Graphics**: OpenGL 3.3+ compatible graphics card
- **Operating System**: Windows, macOS, or Linux
- **Memory**: 4GB RAM recommended

## Dependencies

The project uses the following key libraries:
- **LWJGL 3.3.1**: OpenGL, GLFW, OpenAL, STB, Assimp bindings
- **JOML**: Java OpenGL Math Library for 3D math operations  
- **Bullet Physics**: Physics simulation via LibBulletJME
- **ImGui**: Immediate mode GUI library for Java
- **Gradle**: Build automation and dependency management

## Installation & Running

### Option 1: Using Gradle (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/maxsunc/BTD3D.git
   cd BTD3D
   ```

2. **Build and run:**
   ```bash
   # Build the JAR file
   ./gradlew shadowJar
   
   # Run the game (Windows PowerShell)
   java "-Djava.library.path=./natives/" -jar build/libs/BTD3D-1.0-all.jar
   
   # Alternative for other shells (Git Bash, Linux, macOS)
   java -Djava.library.path="./natives/" -jar build/libs/BTD3D-1.0-all.jar
   ```

### Option 2: Using the provided script

1. **Build the shadow JAR first:**
   ```bash
   ./gradlew shadowJar
   ```

2. **Copy the JAR to root directory as `game.jar`:**
   ```bash
   cp build/libs/BTD3D-1.0-all.jar game.jar
   ```

3. **Run using the provided script:**
   - **Windows:** Double-click `start.sh` or run in Git Bash
   - **Linux/macOS:** `./start.sh`

### Option 3: Development Mode (Simplest)

```bash
./gradlew run
```

## Troubleshooting

If you encounter issues running the game:

1. **PowerShell command errors:** Use `java "-Djava.library.path=./natives/"` (quotes around entire -D parameter)
2. **JAR not found:** Check if the file exists: `ls build/libs/` and use the actual filename
3. **Native library issues:** Try running without library path: `java -jar build/libs/BTD3D-1.0-all.jar`
4. **Permission errors on Windows:** Use `gradlew.bat` instead of `./gradlew`
5. **Java not found:** Ensure JDK 8+ is installed and in your PATH

## Controls

- **WASD**: Pan camera around the map
- **Mouse Drag**: Rotate camera view
- **Mouse Scroll**: Zoom in/out
- **Left Click**: Select/place towers or inspect existing towers
- **Space**: Start the next round
- **1-9**: Spawn different types of bloons (debug mode)
- **Q/E**: Upgrade selected tower (Path 1/Path 2)
- **Escape**: Cancel tower placement

## Game Mechanics

### Tower Types
- **Dart Monkey**: Basic tower with good range and rate
- **Sniper Monkey**: Long range, high damage, slow rate
- **Bomb Tower**: Area damage with explosive projectiles  
- **Super Monkey**: Fast firing, high damage premium tower

### Bloon Types
- **Red → Blue → Green → Yellow → Pink**: Basic progression
- **Black/White**: Special resistances
- **Lead**: Requires explosive damage
- **Zebra/Rainbow**: Multi-layer bloons
- **Ceramic**: Tough outer shell
- **MOAB**: Massive airship boss

### Economy
- Earn money by popping bloons
- Spend money on towers and upgrades
- Bonus money at the end of each round
- Lose lives when bloons reach the exit

## Project Structure

```
src/main/java/me/ChristopherW/
├── core/           # Engine systems (rendering, audio, input)
├── lighting/       # Lighting system
├── process/        # Main game loop and launcher
└── custom/         # Game-specific logic (towers, bloons, UI)
```

## Building from Source

1. **Prerequisites:** Java JDK 8+ and Git
2. **Clone:** `git clone https://github.com/maxsunc/BTD3D.git`
3. **Build:** `./gradlew build`
4. **Create executable JAR:** `./gradlew shadowJar`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is open source. Please check the repository for license details.

## Acknowledgments

- **LWJGL Team** for the excellent OpenGL bindings
- **Ninja Kiwi** for the original Bloons Tower Defense concept
- **Bullet Physics** for realistic physics simulation
- **ImGui** for the clean immediate mode GUI system
