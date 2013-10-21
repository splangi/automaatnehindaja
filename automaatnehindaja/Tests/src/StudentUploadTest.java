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
  private String uploadFilePath = "/home/ubuntu/testylesanne.py";

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
    
    WebElement pass = driver.findElement(By.name("j_password"));
    pass.clear();
    pass.sendKeys("password");
    pass.submit();
    
    WebDriverWait wait = new WebDriverWait(driver, 10);
    WebElement ylesanne1 = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Ülesanne 1")));
    ylesanne1.click(); 
    
    WebElement fileInput = (new WebDriverWait(driver, 10)).
    	until(ExpectedConditions.visibilityOfElementLocated(By.name("file")));
    fileInput.sendKeys(uploadFilePath);
    
    new Select(driver.findElement(By.name("language"))).selectByVisibleText("Python 2.7");
    driver.findElement(By.cssSelector("button")).click();
    
    (new WebDriverWait(driver, 10)).
    	until(ExpectedConditions.visibilityOfElementLocated(By.id("resultOk")));
    
    driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
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
