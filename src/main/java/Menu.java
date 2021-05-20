
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class Menu {

  public static final String ADD_A_PRODUCT = "Add a Product";
  public static final String LIST_ALL_PRODUCTS = "Display all products";
  public static final String QUIT_TEXT = "Quit";

  public enum MenuOption {
    a(ADD_A_PRODUCT),
    b(LIST_ALL_PRODUCTS),
    c(QUIT_TEXT);

    private final String optionText;

    MenuOption(String optionText) {
      this.optionText = optionText;
    }

    public String toString() {
      return this.name() + ". " + this.optionText;
    }
  }

  @Override
  public String toString() {
    String output = "";
    for (MenuOption option : MenuOption.values()) {
      output += option.toString() + "\n";
    }
    return output;
  }

  public void promptUntilDone() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.launchacademy.wishlist");
    EntityManager em = emf.createEntityManager();
    MenuOption input = null;
    Scanner scanner = new Scanner(System.in);
    do {
      System.out.println(this.toString());
      System.out.print("> ");
      try {
        input = MenuOption.valueOf(scanner.nextLine());
      } catch (IllegalArgumentException error) {
        System.out.println("Please choose a valid option.");
      }

      if (input == MenuOption.a) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Product>> violations;
        do {
          System.out.println("What is the name of the product?");
          String productName = scanner.nextLine();
          while (productName.trim().equals("")) {
            System.out.println("Please provide a valid product name:");
            productName = scanner.nextLine();
          }

          List<Product> uniqueProduct;
          do {
            TypedQuery<Product> query = em
                .createQuery("SELECT p FROM Product p WHERE name='" + productName + "'",
                    Product.class);
            uniqueProduct = query.getResultList();
            if (uniqueProduct.size() > 0) {
              System.out.println("Product name already exists! Please enter a unique name:");
              productName = scanner.nextLine();
            }
          } while (uniqueProduct.size() > 0);

          System.out.println("What is the price?");
          double productPrice = scanner.nextDouble();
          scanner.nextLine();

          System.out.println("What is the url?");
          String productUrl = scanner.nextLine();

          Product newProduct = new Product();
          newProduct.setName(productName);
          newProduct.setPrice(productPrice);
          newProduct.setUrl(productUrl);

          violations = validator.validate(newProduct);

          if (violations.size() > 0) {
            for (ConstraintViolation<Product> violation : violations) {
              System.out.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
          } else {
            System.out.println("Ready to persist.");

            em.getTransaction().begin();
            em.persist(newProduct);
            em.getTransaction().commit();
            System.out.println(newProduct.getId());
          }
        } while (violations.size() > 0);
      } else if (input == MenuOption.b) {
        TypedQuery<Product> productsQuery = em
            .createQuery("SELECT p FROM Product p ORDER BY name", Product.class);
        List<Product> productsList = productsQuery.getResultList();
        if (productsList.size() > 0) {
          for (Product product : productsList) {
            System.out.println("Name: " + product.getName());
            System.out.println("Price: " + product.getPrice());
            System.out.println("URL: " + product.getUrl());
            System.out.println("------------");
          }
        } else {
          System.out.println("No products found.");
        }
      }
    } while (input != MenuOption.c);
    em.close();
    emf.close();
    System.out.println("Thanks! Come back soon ya hear!");
  }
}
