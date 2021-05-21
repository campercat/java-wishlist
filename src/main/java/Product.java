import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name="products")
public class Product {
  @Id
  @SequenceGenerator(name="product_generator", sequenceName="products_id_seq", allocationSize = 1)
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="product_generator")
  @Column(name="id", nullable=false, unique=true)
  private Long id;

  // NotBlank is good for checking empty strings
  @NotBlank
  @Column(name="name", nullable = false, length = 50)
  private String name;

  @NotNull
  @Positive
  @Column(name="price", nullable = false)
  private double price;

  @URL(regexp = "^(http|https).*||null", message = "Your URL must use a protocol of http or https")
  @Column(name="url")
  private String url;

  @ManyToOne
  @JoinColumn(name="category_id", nullable = false)
  private Category category;

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
