# How to Start the Server

## Problem: ERR_CONNECTION_REFUSED

This error means the server is **NOT running**. Follow these steps:

## Step 1: Check if Port 8080 is Free

Open PowerShell/Command Prompt and run:
```powershell
netstat -ano | findstr :8080
```

If you see output, something is using port 8080. Either:
- Stop that application, OR
- Change port in `application.properties` to another (e.g., 8081)

## Step 2: Start Server from IntelliJ IDEA

1. **Find the main class:**
   - Open `src/main/java/com/university/coursemanagement/CourseManagementApplication.java`

2. **Run it:**
   - Right-click on the file
   - Select **Run 'CourseManagementApplication.main()'**
   - OR click the green play button next to `main` method

3. **Check the console:**
   - Look for: `Started CourseManagementApplication in X.XXX seconds`
   - If you see errors, read them carefully

## Step 3: Start Server from Terminal (Alternative)

Open terminal in project directory:
```bash
cd "C:\Users\sun4u\OneDrive\Рабочий стол\Навчання\Прикладне програмування\lab-4\BE"
mvn spring-boot:run
```

Wait for:
```
Started CourseManagementApplication in X.XXX seconds
```

## Step 4: Verify Server is Running

1. **Check console output:**
   - Should see: `Tomcat started on port(s): 8080 (http)`

2. **Test in browser:**
   - Open: `http://localhost:8080/api/health`
   - Should see: `{"status":"UP","message":"Server is running"}`

3. **Test with curl (PowerShell):**
   ```powershell
   curl http://localhost:8080/api/health
   ```

## Common Startup Errors

### Error: "Port 8080 already in use"
**Solution:**
- Find and kill process: `netstat -ano | findstr :8080`
- Note the PID (last number)
- Kill it: `taskkill /PID <number> /F`
- Or change port in `application.properties`

### Error: "Bean creation failed"
**Solution:**
- Check if all dependencies are downloaded
- Run: `mvn clean install`
- Check console for specific error message

### Error: "ClassNotFoundException"
**Solution:**
- Rebuild project: `Build → Rebuild Project`
- Or: `mvn clean compile`

### Error: "Lombok not working"
**Solution:**
- Install Lombok plugin in IntelliJ
- Enable annotation processing
- See `INTELLIJ_SETUP.md`

## Step 5: Check Logs

If server starts but you still can't connect:

1. **Check IntelliJ console** for errors
2. **Check application logs** (if configured)
3. **Check Windows Firewall:**
   - Windows Security → Firewall
   - Allow Java through firewall if prompted

## Quick Test Commands

```powershell
# Check if port is in use
netstat -ano | findstr :8080

# Test server (after starting)
curl http://localhost:8080/api/health

# Check Java version
java -version

# Clean and rebuild
mvn clean install
```

## Still Not Working?

1. **Check IntelliJ Run Configuration:**
   - Run → Edit Configurations
   - Main class: `com.university.coursemanagement.CourseManagementApplication`
   - Working directory: Project root

2. **Try running from terminal:**
   ```bash
   mvn clean spring-boot:run
   ```

3. **Check for compilation errors:**
   - Build → Rebuild Project
   - Fix any red errors

4. **Check Java version:**
   - Must be Java 17 or higher
   - `java -version` should show 17.x.x

