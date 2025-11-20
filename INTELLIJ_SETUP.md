# IntelliJ IDEA Setup Instructions

## Fix for `ExceptionInInitializerError` with Lombok

### Step 1: Configure Project SDK
1. Open **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. Go to **Project** tab
3. Set **Project SDK** to **Java 17** (or higher)
4. Set **Project language level** to **17 - Sealed types, always-strict floating-point semantics**
5. Click **OK**

### Step 2: Enable Annotation Processing
1. Open **File** → **Settings** (Ctrl+Alt+S)
2. Navigate to **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
3. Check **Enable annotation processing**
4. Set **Annotation processing profile** to **Default**
5. Click **OK**

### Step 3: Install Lombok Plugin
1. Open **File** → **Settings** → **Plugins**
2. Search for **"Lombok"**
3. Install **Lombok Plugin** (by Michail Plushnikov)
4. Restart IntelliJ IDEA

### Step 4: Reimport Maven Project
1. Open **Maven** tool window (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** button (circular arrow icon)
3. Wait for dependencies to download

### Step 5: Invalidate Caches
1. Go to **File** → **Invalidate Caches...**
2. Check **Clear file system cache and Local History**
3. Click **Invalidate and Restart**

### Step 6: Verify Java Version
1. Open terminal in IntelliJ IDEA (Alt+F12)
2. Run: `java -version`
3. Should show Java 17 or higher
4. If not, set JAVA_HOME environment variable to Java 17

### Step 7: Build Project
1. Go to **Build** → **Rebuild Project**
2. Wait for build to complete
3. Check for any errors in **Build** tool window

## Alternative: Use Maven to Run
If IntelliJ still has issues, run from terminal:

```bash
mvn clean compile
mvn spring-boot:run
```

## Troubleshooting

### If error persists:
1. Delete `.idea` folder (close IntelliJ first)
2. Delete `target` folder
3. Reopen project in IntelliJ
4. Let IntelliJ reimport everything

### Check Java Version:
- IntelliJ uses its own JDK for compilation
- Go to **File** → **Project Structure** → **SDKs**
- Add JDK 17 if not present
- Set it as project SDK

### Lombok Not Working:
- Ensure Lombok plugin is installed and enabled
- Check **File** → **Settings** → **Build, Execution, Deployment** → **Compiler** → **Annotation Processors** is enabled
- Restart IntelliJ after enabling

