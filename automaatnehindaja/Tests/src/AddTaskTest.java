import java.util.ArrayList;
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

public class AddTaskTest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://ec2-54-237-98-146.compute-1.amazonaws.com";
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
  }

  @Test
  public void testAddTask() throws Exception {
	driver.get(baseUrl + "/automaatnehindaja/");
    WebElement username = driver.findElement(By.name("j_username"));
    username.clear();
    username.sendKeys("henri");
    
    WebElement pass = driver.findElement(By.name("j_password"));
    pass.clear();
    pass.sendKeys("passwd");
    pass.submit();
    
    driver.findElement(By.xpath("//div[@id='buttonDiv']/a[3]/button")).click();
    driver.findElement(By.cssSelector("button.Zebra_DatePicker_Icon.Zebra_DatePicker_Icon_Inside")).click();
    
    WebElement calendar = (new WebDriverWait(driver, 10)).
        	until(ExpectedConditions.visibilityOfElementLocated(By.className("Zebra_DatePicker")));
    
    ArrayList<WebElement> dates = (ArrayList<WebElement>) calendar.findElements(By.tagName("td"));
    
    for (int i = 0; i < dates.size(); i++) {
    	if (!dates.get(i).getAttribute("class").startsWith("dp")) {
    		dates.get(i).click();
    		break;
    	}
    }
    
    driver.findElement(By.id("name")).clear();
    driver.findElement(By.id("name")).sendKeys("Ülesanne 2");
    driver.findElement(By.id("desc")).clear();
    driver.findElement(By.id("desc")).sendKeys("Test");
    driver.findElement(By.id("input0")).clear();
    driver.findElement(By.id("input0")).sendKeys("Test");
    driver.findElement(By.id("output0")).clear();
    driver.findElement(By.id("output0")).sendKeys("Test");
    
    WebElement addio = driver.findElement(By.xpath("//button[@onclick='addio()']"));
    WebElement submit = driver.findElement(By.xpath("//button[@onclick='checkFields()']"));
    addio.click();
    submit.click();
    
    (new WebDriverWait(driver, 10)).
	until(ExpectedConditions.textToBePresentInElement(By.id("message"), "Palun täitke kõik väljad!"));
    
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
