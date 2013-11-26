import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StudentUploadTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  private String uploadFilePath = "/home/ubuntu/web/tests/splangi_Maatriksi transponeerimine.py";

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://ec2-54-237-98-146.compute-1.amazonaws.com";
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
  }

  @Test
  public void testUpload() throws Exception {
    driver.get(baseUrl + "/automaatnehindaja/");
    WebElement username = driver.findElement(By.name("j_username"));
    username.clear();
    username.sendKeys("splangi");
    
    WebElement pass = driver.findElement(By.name("password"));
    pass.clear();
    pass.sendKeys("password");
    pass.submit();
    
    WebDriverWait wait = new WebDriverWait(driver, 10);
    WebElement ylesanded = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='#tasksview']")));
    ylesanded.click();
    
    //#mainlist > li:nth-child(2) > a:nth-child(1)
    
    
    
    WebDriverWait wait2 = new WebDriverWait(driver, 10);
    WebElement courses = wait2.until(ExpectedConditions.elementToBeClickable(By.id("courses")));
    new Select(driver.findElement(By.id("courses"))).selectByVisibleText("kursusJ");
    
    WebDriverWait wait3 = new WebDriverWait(driver, 10);
    WebElement ylesanne = wait3.until(ExpectedConditions.elementToBeClickable(By.linkText("uus")));
    ylesanne.click();
    
    WebElement fileInput = (new WebDriverWait(driver, 10)).
    	until(ExpectedConditions.visibilityOfElementLocated(By.name("file")));
    fileInput.sendKeys(uploadFilePath);
    
    new Select(driver.findElement(By.name("language"))).selectByVisibleText("Python 2.7");
    driver.findElement(By.cssSelector("button")).click();
    
    (new WebDriverWait(driver, 10)).
    	until(ExpectedConditions.visibilityOfElementLocated(By.id("resultOk")));
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }
}
