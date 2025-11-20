# Quick Fix for IntelliJ IDEA Error

## Problem
`java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`

## Solution Steps (Do ALL of these):

### 1. Check Java Version in IntelliJ
- **File** → **Project Structure** (Ctrl+Alt+Shift+S)
- **Project** tab:
  - **Project SDK**: Must be **Java 17** or higher (NOT Java 8!)
  - **Project language level**: **17**
- **SDKs** tab: If Java 17 is not listed, click **+** → **Add JDK** → Select Java 17 installation

### 2. Enable Annotation Processing
- **File** → **Settings** (Ctrl+Alt+S)
- **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
- ✅ Check **"Enable annotation processing"**
- **Store generated sources relative to**: Select **Module output directory**
- Click **OK**

### 3. Install Lombok Plugin
- **File** → **Settings** → **Plugins**
- Search **"Lombok"**
- Install **Lombok Plugin** (by Michail Plushnikov)
- **Restart IntelliJ IDEA**

### 4. Reimport Maven
- Open **Maven** tool window (right sidebar)
- Click **Reload All Maven Projects** (circular arrow icon)
- Wait for download to complete

### 5. Clean and Rebuild
- **Build** → **Clean Project**
- **Build** → **Rebuild Project**
- Wait for completion

### 6. Invalidate Caches
- **File** → **Invalidate Caches...**
- Check **"Clear file system cache and Local History"**
- Click **"Invalidate and Restart"**

## Alternative: Run from Terminal

If IntelliJ still fails, use terminal:

```bash
# Navigate to project directory
cd "C:\Users\sun4u\OneDrive\Рабочий стол\Навчання\Прикладне програмування\lab-4\BE"

# Clean and compile
mvn clean compile

# Run application
mvn spring-boot:run
```

## Verify Java Version

In IntelliJ terminal (Alt+F12):
```bash
java -version
```

Should show:
```
java version "17.x.x" or higher
```

If it shows Java 8, you need to:
1. Install Java 17 from https://adoptium.net/
2. Set JAVA_HOME environment variable to Java 17
3. Restart IntelliJ IDEA

## Still Not Working?

1. Close IntelliJ IDEA
2. Delete `.idea` folder in project
3. Delete `target` folder
4. Reopen project in IntelliJ
5. Let it reimport everything
6. Follow steps 1-6 above

