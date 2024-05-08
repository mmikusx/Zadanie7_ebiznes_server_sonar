package controllers

import javax.inject._
import play.api._
import play.api.mvc._

case class Product(id: Long, name: String, price: Double)

@Singleton
class ProductController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  var products: List[Product] = List(
    Product(1, "Product 1", 10.0),
    Product(2, "Product 2", 20.0),
    Product(3, "Product 3", 30.0)
  )

  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(products))
  }

  def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id).map { product =>
      Ok(views.html.show(product))
    }.getOrElse(NotFound)
  }

  def create = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.create())
  }

  def store = Action { implicit request: Request[AnyContent] =>
    val body = request.body
    val formParams: Map[String, Seq[String]] = body.asFormUrlEncoded.getOrElse(Map.empty)
    val id = products.map(_.id).max + 1
    val name = formParams.get("name").flatMap(_.headOption).getOrElse("")
    val price = formParams.get("price").flatMap(_.headOption).map(_.toDouble).getOrElse(0.0)
    val newProduct = Product(id, name, price)
    products = products :+ newProduct
    Redirect(routes.ProductController.index)
  }

  def edit(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id).map { product =>
      Ok(views.html.edit(product))
    }.getOrElse(NotFound)
  }

  def update(id: Long) = Action { implicit request: Request[AnyContent] =>
    val body = request.body
    val formParams = body.asFormUrlEncoded.getOrElse(Map.empty[String, Seq[String]])
    val name = formParams.get("name").flatMap(_.headOption).getOrElse("")
    val price = formParams.get("price").flatMap(_.headOption).map(_.toDouble).getOrElse(0.0)
    products = products.map {
      case p if p.id == id => Product(id, name, price)
      case p => p
    }
    Redirect(routes.ProductController.show(id))
  }

  def delete(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id).map { product =>
      Ok(views.html.delete(product))
    }.getOrElse(NotFound)
  }

  def destroy(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id) match {
      case Some(_) =>
        products = products.filterNot(_.id == id)
        Redirect(routes.ProductController.index)
      case None =>
        NotFound
    }
  }

  def getAllProducts(): List[Product] = {
    println(products)
    products
  }
}
