package com.qa;

//import com.aventstack.extentreports.testng.listener.ExtentITestListenerAdapter;
import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.MobileElement;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.util.*;

//@Listeners(ExtentITestListenerAdapter.class)
public class BaseTest {
    protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal <Properties> pros= new ThreadLocal<Properties>();
    protected static ThreadLocal <HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
    protected static ThreadLocal <String> platform = new ThreadLocal<String>();
    protected static ThreadLocal <String> dateTime = new ThreadLocal<String>();
    protected static ThreadLocal <String> deviceName = new ThreadLocal<String>();
    private static AppiumDriverLocalService server;
//    static Logger log = LogManager.getLogger(BaseTest.class.getName()); //write in TestUtil class

    TestUtils utils = new TestUtils();

    public AppiumDriver getDriver() {
        return driver.get();
    }

    public void setDriver(AppiumDriver driver2) {
        driver.set(driver2);
    }

    public Properties getProps() {
        return pros.get();
    }

    public void setProps(Properties props2) {
        pros.set(props2);
    }

    public HashMap<String, String> getStrings() {
        return strings.get();
    }

    public void setStrings(HashMap<String, String> strings2) {
        strings.set(strings2);
    }

    public String getPlatform() {
        return platform.get();
    }

    public void setPlatform(String platform2) {
        platform.set(platform2);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String dateTime2) {
        dateTime.set(dateTime2);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public void setDeviceName(String deviceName2) {
        deviceName.set(deviceName2);
    }

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    @BeforeSuite
    public void beforeSuite() throws Exception, Exception {
//        // kill procee
//        lsof -P | grep ':4723' | awk '{print $2}' | xargs kill -9
        ThreadContext.put("ROUTINGKEY", "ServerLogs");
		server = getAppiumService(); // -> If using Mac, uncomment this statement and comment below statement
//        server = getAppiumServerDefault(); // -> If using Windows, uncomment this statement and comment above statement
        if(!checkIfAppiumServerIsRunnning(4723)) {
            server.start();
            server.clearOutPutStreams(); // -> Comment this if you don't want to see server logs in the console
            utils.log().info("Appium server started");
        } else {
            utils.log().info("Appium server already running");
        }
    }
    public boolean checkIfAppiumServerIsRunnning(int port) throws Exception {
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            System.out.println("1");
            isAppiumServerRunning = true;
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }
    // for Windows
    public AppiumDriverLocalService getAppiumServerDefault() {
        return AppiumDriverLocalService.buildDefaultService();
    }

    // for Mac. Update the paths as per your Mac setup
    public AppiumDriverLocalService getAppiumService() {
        /*  Để IOS chạy được thì cần brew install carthage và set biến PATH vào enviroment
        Khi mình cài appium 2.0.56 với node: v16.19.1   và npm: 8.19.3 thì lúc stảt sẻver cứ bị lỗi APPIUM spawn npm ENOENT
        thì mình cài lại appium với lênh npm install -g appium  >>> không thấy bị nữa Chắc là do tương quan các
        bản appium và npm, node với nhau
        Lúc chạy xong thì cần xoa process, cụ thể:
        với mac:  lsof -P | grep ':4723' | awk '{print $2}' | xargs kill -9
        với win: Netstat -ano|findstr “PID:4723”
Task        kill /pid {ma pid lay dc o lent tren} /f
        * */
        HashMap<String, String> environment = new HashMap<String, String>();
        environment.put("JAVA_HOME", "/Library/Java/JavaVirtualMachines/jdk-11.0.16.jdk/Contents/Home");
        environment.put("ANDROID_HOME", "/Users/hoangthuhuyen/Library/Android/sdk");
        environment.put("PATH", "/Users/hoangthuhuyen/Documents/Setup/apache-maven-3.9.0/bin /Library/" +
                "Java/JavaVirtualMachines/jdk-11.0.16.jdk/Contents/Home/bin /Users/hoangthuhuyen/" +
                "Library/Android/sdk/tools /Users/hoangthuhuyen/Library/Android/sdk/platform-tools " +
                "/opt/homebrew/bin /usr/local/bin /System/Cryptexes/App/usr/bin /usr/bin /bin /usr" +
                "/sbin /sbin /Library/Apple/usr/bin /usr/local/mysql/bin/ /Users/hoangthuhuyen/Documents" +
                "/Setup/apache-maven-3.9.0/bin /Library/Java/JavaVirtualMachines/jdk-11.0.16.jdk/Contents/Home/bin " +
                "/Users/hoangthuhuyen/Library/Android/sdk/tools /Users/hoangthuhuyen/Library/Android/sdk/platform-tools " +
                "/opt/homebrew/bin /Users/hoangthuhuyen/Documents/Redis/redis-stack-server-7/bin /bin"
                + System.getenv("PATH"));

//        environment.put("NODE_PATH", "/opt/homebrew/bin/node");
//        environment.put("NPM_PATH", "/opt/homebrew/bin/npm");

        AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder();
        appiumServiceBuilder.withArgument(() -> "--base-path", "/wd/hub");// với appium < 2 thì thêm cái này
//                .withIPAddress("127.0.0.1")
        appiumServiceBuilder.usingPort(4723)
                //ban chat cua dong duoi la giong nhu ơ terminal go: appium --use-plugins execute-driver
                .usingDriverExecutable(new File("/opt/homebrew/bin/node"))
//                .withArgument(() ->"--use-plugins", "execute-driver") // appim >=2 mới cần dùng, và là opiton thôi
                . withAppiumJS(new File("/opt/homebrew/bin/appium"))
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withEnvironment(environment)
                .withLogFile(new File(System.getProperty("user.dir")
                        + "/target/resources/appium_server_logs" + Thread.currentThread().getId()));
        return AppiumDriverLocalService.buildService(appiumServiceBuilder);

//        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
//                .usingDriverExecutable(new File("/opt/homebrew/bin/node"))
//                .withAppiumJS(new File("/opt/homebrew/bin/appium"))
//                .usingPort(4723)
//                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
// 				.withArgument(() -> "--allow-insecure","chromedriver_autodownload")
//                .withEnvironment(environment)
//                .withLogFile(new File("ServerLogs/server.log")));
    }

    @BeforeTest
    @Parameters({"emulator", "platformName", "udid", "deviceName", "systemPort",
            "chromeDriverPort", "wdaLocalPort", "webkitDebugProxyPort"})
    public void beforeTest(@Optional("androidOnly")String emulator, String platformName, String udid, String deviceName,
                @Optional("androidOnly")String systemPort, @Optional("androidOnly")String chromeDriverPort,
                @Optional("iOSOnly")String wdaLocalPort, @Optional("iOSOnly")String webkitDebugProxyPort) throws Exception {


//        log.info("this is infor message");
//        log.error("this is error message");
//        log.debug("this is debug message");
//        log.warn("this is warn message");


        AppiumDriver driver;
        InputStream inputStream = null;
        InputStream stringsis = null;
        Properties pros = new Properties();

        String strFile = "logs" + File.separator + platformName + "_" + deviceName;
        File logFile = new File(strFile);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        //route logs to separate file for each thread
        ThreadContext.put("ROUTINGKEY", strFile);
        utils.log().info("log path: " + strFile);

        try {
            setDateTime(utils.dateTime());
            setPlatform(platformName);
            setProps(pros);
            String proFileName = "config.properties";
            String xmlFileName = "strings/strings.xml";
            inputStream = getClass().getClassLoader().getResourceAsStream(proFileName);
            pros.load(inputStream);

            stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            setStrings(utils.parseStringXML(stringsis));

            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
            caps.setCapability("newCommandTimeout",3000);

            URL url;
            url = new URL(pros.getProperty("appiumURL") + "4723/wd/hub"); // dung neu muon parralel treen 1 server instance

            caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            switch(platformName)
            {
                case "Android":
                {
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, pros.getProperty("androidAutomationName"));
//              String appUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
//                + File.separator + "resources" + File.separator + "app" + File.separator+ "Android.SauceLabs.Mobile.Sample.app.2.2.1.apk";
                    URL AroidAppUrl = getClass().getClassLoader().getResource(pros.getProperty("androidAppLocation"));
//                String AroidAppUrl = getClass().getResource(pros.getProperty("androidAppLocation")).getFile();
                    caps.setCapability("appPackage",pros.getProperty("androidAppPackage"));
                    caps.setCapability("appActivity",pros.getProperty("androidAppActivity"));
//				đoạn dứoi là để nếu là emulator thì mình emulatỏ sẽ được bật tự đông, ko cần manually
                    if(emulator.equalsIgnoreCase("true")) {
                        //cung co the set plaform version cho android
                        caps.setCapability("avd", deviceName);
                    }
                    else{
                        //dung cho real device
                        caps.setCapability("udid", udid);
                    }
                    caps.setCapability("systemPort", systemPort);
                    caps.setCapability("appium:chromeDriverPort", chromeDriverPort);
                    caps.setCapability("avdLaunchTimeout", 120000);
                    caps.setCapability(MobileCapabilityType.APP, AroidAppUrl);
//                    url = new URL(pros.getProperty("appiumURL") + "4723");
                    utils.log().info("this is log fir android");
                    driver = new AndroidDriver(url, caps);
                    break;
                }
                case "iOS":
                {
//                    caps.setCapability("udid", udid);//dung cho real devide
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, pros.getProperty("iOSAutomationName"));
                    caps.setCapability("wdaLocalPort", wdaLocalPort);
                    caps.setCapability("appium:webkitDebugProxyPort", webkitDebugProxyPort);
//                caps.setCapability("bundleId",pros.getProperty("iOSBundleId"));
////                    url = new URL(pros.getProperty("appiumURL") + "4724");

                   String iOSAppUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                + File.separator + "resources" + File.separator + "app" + File.separator+ "SwagLabsMobileApp.app";
                    caps.setCapability("app", iOSAppUrl);
                    url = new URL(pros.getProperty("appiumURL") + "4723");

                    utils.log().info("this is log fir IOS");
                    driver = new IOSDriver(url, caps);
                    break;
                }
                default:
                    throw new Exception("invalid platform - " + platformName);
            }
            setDriver(driver);
            System.out.println("sessionid: " + getDriver().getSessionId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if(stringsis != null) {
                stringsis.close();
            }
        }
    }
    @BeforeMethod
    public void beforeMethod() {
        ((CanRecordScreen) getDriver()).startRecordingScreen();
    }

    //stop video capturing and create *.mp4 file
    @AfterMethod
    public synchronized void afterMethod(ITestResult result) throws Exception {
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();
        if(result.getStatus() == 2)//trang thai fail
        {
            Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
            String dirPath = "videos" + File.separator + params.get("platformName") + "_" + params.get("deviceName")
                    + File.separator + getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName();

            File videoDir = new File(dirPath);

            synchronized (videoDir) {
                if (!videoDir.exists()) {
                    videoDir.mkdirs();
                }
            }
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
                //            stream.write(Base64.decodeBase64(media));
                stream.close();
                //            utils.log().info("video path: " + videoDir + File.separator + result.getName() + ".mp4");
            } catch (Exception e) {
                //            utils.log().error("error during video capture" + e.toString());
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }

    public void waitForVisibility(MobileElement e) {
        WebDriverWait wait = new WebDriverWait(getDriver(), TestUtils.WAIT);
        wait.until(ExpectedConditions.visibilityOf(e));
    }
    public void click(MobileElement e) {
        waitForVisibility(e);
        e.click();
    }

    public void click(MobileElement e, String msg) {
        waitForVisibility(e);
        utils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        e.click();
    }
    public void clear(MobileElement e) {
        waitForVisibility(e);
        e.clear();
    }

    public void sendKeys(MobileElement e, String txt) {
        waitForVisibility(e);
        e.sendKeys(txt);
    }
    public void sendKeys(MobileElement e, String txt, String msg) {
        waitForVisibility(e);
        utils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        e.sendKeys(txt);
    }
    public String getAttribute(MobileElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }
    public String getText(MobileElement e, String msg) {
        String txt = null;
        switch(getPlatform()) {
            case "Android":
                txt = getAttribute(e, "text");
                break;
            case "iOS":
                txt = getAttribute(e, "label");
                break;
        }
        utils.log().info(msg + txt);
        ExtentReport.getTest().log(Status.INFO, msg + txt);
        return txt;
    }

    public void closeApp() {
        ((InteractsWithApps)getDriver()).closeApp();
    }

    public void launchApp() {
        ((InteractsWithApps)getDriver()).launchApp();
    }

    public MobileElement scrollToElement() {
//        return (MobileElement) ((FindsByAndroidUIAutomator) driver).findElementByAndroidUIAutomator(
//                "new UiScrollable(new UiSelector()" + ".description(\"test-Inventory item page\")).scrollIntoView("
//                        + "new UiSelector().description(\"test-Price\"));");
        return (MobileElement) ((FindsByAndroidUIAutomator) getDriver()).findElementByAndroidUIAutomator(
                "new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().description(\"test-Price\"));");
    }

    public void iOSScrollToElement() {
        RemoteWebElement element = (RemoteWebElement)getDriver().findElement(By.name("test-ADD TO CART"));
        String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", elementID);
	  scrollObject.put("direction", "down");
//	  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//	  scrollObject.put("name", "test-ADD TO CART");
//        scrollObject.put("toVisible", "sdfnjksdnfkld");
        getDriver().executeScript("mobile:scroll", scrollObject);
    }


    @AfterTest(alwaysRun = true)
    public void afterTest() {
        getDriver().quit();
    }
}

