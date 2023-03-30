package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
//import com.qa.utils.TestUtils;
//import org.json.JSONObject;
//import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class LoginTests extends BaseTest{
	LoginPage loginPage;
	ProductsPage productsPage;
	JSONObject loginUsers;
//	TestUtils utils = new TestUtils();
	
	  @BeforeClass
	  public void beforeClass() throws Exception {
		  InputStream datais = null;
		  try {
			  String dataFileName = "data/loginUsers.json";
			  datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
			  JSONTokener tokener = new JSONTokener(datais);
			  loginUsers = new JSONObject(tokener);
		  } catch(Exception e) {
			  e.printStackTrace();
			  throw e;
		  } finally {
			  if(datais != null) {
				  datais.close();
			  }
		  }
		  closeApp();
		  launchApp();
	  }
//
////	  @AfterClass
////	  public void afterClass() {
////	  }
////
	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  System.out.print("\n" + "****** starting test:" + m.getName() + "******" + "\n");
		  loginPage = new LoginPage();
	  }
//
////	  @AfterMethod
////	  public void afterMethod() {
////	  }
	  
	  @Test
	  public void invalidUserName() {
//		  loginPage = new LoginPage();
//		  loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
//		  loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
//		  loginPage.pressLoginBtn();
//		  try {
			  loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
			  loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
			  loginPage.pressLoginBtn();

			  String actualErrTxt = loginPage.getErrTxt();
			  String expectedErrTxt = getStrings().get("err_invalid_username_or_password1");

			  Assert.assertEquals(actualErrTxt, expectedErrTxt);
//		  }

//		  catch(Exception e)
//		  {
//		  	StringWriter sw =new StringWriter();
//		  	PrintWriter pw = new PrintWriter(sw);
//			e.printStackTrace(pw);
//			System.out.println(sw.toString());
//			Assert.fail(sw.toString());
//		  }
	  }
	  
	  @Test
	  public void invalidPassword() {
		  loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("username"));
		  loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
		  loginPage.pressLoginBtn();

		  String actualErrTxt = loginPage.getErrTxt();
		  String expectedErrTxt = getStrings().get("err_invalid_username_or_password");
		  Assert.assertEquals(actualErrTxt, expectedErrTxt);
	  }

	  @Test
	  public void successfulLogin() {
		  loginPage.enterUserName(loginUsers.getJSONObject("validUser").getString("username"));
		  loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
		  productsPage = loginPage.pressLoginBtn();

		  String actualProductTile = productsPage.getTitle();
		  String expectedErrTxt = getStrings().get("product_title");

		  Assert.assertEquals(actualProductTile, expectedErrTxt);
	  }
}
