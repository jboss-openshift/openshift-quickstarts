package org.jboss.as.quickstarts.datagrid.carmart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.net.URL;

import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Test for CarMart example using Drone Arquillian extension and Graphenebrowser framework for testing app UI
 * 
 * Scenario (only basic operations):
 * Start CarMart, try to add new car, check if new car is added, try to remove added car, check if car was successfully removed
 *
 * @author tsykora@redhat.com
 * @author jholusa@redhat.com
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CarmartTest {

    @FindBy(id = "j_idt6")
    private WebElement FORM_ID;
    @FindByJQuery(":contains('JDG CarMart')")
    private WebElement welcomeCarmart;
    @FindByJQuery("a:contains('New car')")
    private WebElement newCarLink;
    @FindByJQuery("#j_idt6:contains('Add a new car to the Car Mart')")
    private WebElement addNewCar;
    @FindByJQuery("#j_idt6:contains('List of available cars')")
    private WebElement listOfCars;
    @FindByJQuery("#j_idt6:contains('Number Plate')")
    private WebElement numberPlateLabel;
    @FindByJQuery(":input[type=text]:eq(0)")
    private WebElement numberPlateInput;
    @FindByJQuery(":input[type=text]:eq(1)")
    private WebElement brandInput;
    @FindByJQuery(":input[type=text]:eq(2)")
    private WebElement colorInput;
    @FindByJQuery(":input[type=text]:eq(3)")
    private WebElement displacementInput;
    @FindByJQuery("table:eq(1) tr:eq(1) td:eq(1)")
    private WebElement storesCountElement;
    @FindByJQuery(":submit")
    private WebElement submitCarButton;
    @FindByJQuery(":contains('1B2 1234')")
    private WebElement addedCar;
    @FindByJQuery("a:contains('View'):eq(5)")
    private WebElement viewAddedCar;
    @FindByJQuery(":contains('1B2 1234')")
    private WebElement numberPlateInputAdded;
    @FindByJQuery(":contains('TOYOTA')")
    private WebElement brandInputAdded;
    @FindByJQuery(":contains('YELLOW')")
    private WebElement colorInputAdded;
    @FindByJQuery(":contains('15')")
    private WebElement displacementInputAdded;
    @FindByJQuery("a:contains('Remove'):eq(5)")
    private WebElement removeAddedCar;
    @FindByJQuery("a:contains('Home')")
    private WebElement homeLink;

    @Drone
    WebDriver browser;

    @ArquillianResource
    private URL contextPath;
    
    @Deployment(testable = false)
    public static WebArchive createTestDeploymentRemote() {
        return Deployments.createDeployment();
    }

    @Test
    public void basicOperationsTest() {
        browser.get(contextPath.toExternalForm());
        System.out.println("Inside basicOperationsTest, contextPath: " + contextPath);

        waitModel().until().element(FORM_ID).is().present();
        assertTrue("FORM_ID element is not present", FORM_ID.isDisplayed());

        waitModel().until().element(welcomeCarmart).is().present();
        assertTrue("welcomeCarmart is not present", welcomeCarmart.isDisplayed());

        waitModel().until().element(newCarLink).is().present();
        assertTrue("newCarLink is not present", newCarLink.isDisplayed());

        newCarLink.click();
        waitModel().until().element(addNewCar).is().present();

        waitModel().until().element(numberPlateLabel).is().present();
        assertTrue("numberPlateLabel is not present", numberPlateLabel.isDisplayed());

        waitModel().until().element(numberPlateInput).is().present();
        waitModel().until().element(brandInput).is().present();
        waitModel().until().element(colorInput).is().present();
        waitModel().until().element(displacementInput).is().present();

        assertTrue("numberPlateInput is not present", numberPlateInput.isDisplayed());
        assertTrue("brandInput is not present", brandInput.isDisplayed());
        assertTrue("colorInput is not present", colorInput.isDisplayed());
        assertTrue("displacementInput is not present", displacementInput.isDisplayed());

        numberPlateInput.sendKeys("1B2 1234");
        brandInput.sendKeys("TOYOTA");
        colorInput.sendKeys("YELLOW");
        displacementInput.sendKeys("15");

        // statistic table
        waitModel().until().element(storesCountElement).is().present();
        String storesCountBefore = storesCountElement.getText();

        // select all elements of type submit - here only one
        waitModel().until().element(submitCarButton).is().present();
        assertTrue("submitCarButton is not present", submitCarButton.isDisplayed());

        submitCarButton.click();

        // looking for new car
        waitModel().until().element(listOfCars).is().present();

        // see CarManager class for details (only 1 put there)
        waitModel().until().element(storesCountElement).is().present();
        String storesCountAfter = storesCountElement.getText();

        // !!! - the difference must be 2 because in Carmart quickstart
        // there is extra put with number of plates in the same cache, therefore
        // the number of stores is increased by 1 for inserting car and 1 for inserting this extra info !!!
        assertTrue("Number of stores should increase exactly by 1. Not by more or less.",
                Integer.valueOf(storesCountAfter).equals(Integer.valueOf(storesCountBefore) + 2));

        // the 6th view link should be new added car
        waitModel().until().element(viewAddedCar).is().present();
        assertTrue("6th view link for new addedCar is not present", viewAddedCar.isDisplayed());

        // show car info
        viewAddedCar.click();
        waitModel().until().element(addedCar).is().present();

        waitModel().until().element(numberPlateInputAdded).is().present();
        waitModel().until().element(brandInputAdded).is().present();
        waitModel().until().element(colorInputAdded).is().present();
        waitModel().until().element(displacementInputAdded).is().present();

        assertTrue("numberPlateInputAdded is not present....", numberPlateInputAdded.isDisplayed());
        assertTrue("brandInputAdded is not present....", brandInputAdded.isDisplayed());
        assertTrue("colorInputAdded is not present....", colorInputAdded.isDisplayed());
        assertTrue("displacementInputAdded is not present....", displacementInputAdded.isDisplayed());

        // back to home page and remove it
        waitModel().until().element(homeLink).is().present();
        homeLink.click();

        waitModel().until().element(removeAddedCar).is().present();
        assertTrue("6th remove link for new addedCar is not present", removeAddedCar.isDisplayed());

        // remove car
        removeAddedCar.click();

        // check if removed
        try {
           boolean isDisplayed = viewAddedCar.isDisplayed();
           fail("6th view link for new addedCar is STILL present! Should not be!");
        } catch(NoSuchElementException ex) {
           //OK - expected
        }
    }

}
