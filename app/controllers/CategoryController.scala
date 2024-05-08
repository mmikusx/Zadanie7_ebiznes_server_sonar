package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.collection.mutable.ListBuffer

case class Category(id: Long, name: String, products: ListBuffer[Product])

@Singleton
class CategoryController @Inject()(productsController: ProductController, val controllerComponents: ControllerComponents) extends BaseController {

  var categories: ListBuffer[Category] = ListBuffer(
    Category(1, "Category 1", ListBuffer.empty[Product]),
    Category(2, "Category 2", ListBuffer.empty[Product]),
    Category(3, "Category 3", ListBuffer.empty[Product])
  )

  var products: ListBuffer[Product] = ListBuffer.from(productsController.getAllProducts())

  def indexCategory = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.indexCategory(categories.toList))
  }

  def showCategories(id: Long) = Action { implicit request: Request[AnyContent] =>
    products = ListBuffer.from(productsController.getAllProducts())
    categories.find(_.id == id).map { category =>
      Ok(views.html.showCategory(category, products.toList.filterNot(p => category.products.exists(_.id == p.id))))
    }.getOrElse(NotFound)
  }

  def createCategory = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.createCategory())
  }

  def storeCategory = Action { implicit request: Request[AnyContent] =>
    val body = request.body
    val formParams = body.asFormUrlEncoded.getOrElse(Map.empty[String, Seq[String]])
    val name = formParams.get("name").flatMap(_.headOption).getOrElse("")
    val id = categories.map(_.id).max + 1
    val newCategory = Category(id, name, ListBuffer.empty[Product])
    categories += newCategory
    Redirect(routes.CategoryController.indexCategory)
  }

  def addProductToCategory(id: Long) = Action { implicit request: Request[AnyContent] =>
    val body = request.body
    val formParams = body.asFormUrlEncoded.getOrElse(Map.empty[String, Seq[String]])
    val productName = formParams.get("product").flatMap(_.headOption).getOrElse("")
    val categoryOpt = categories.find(_.id == id)
    val productOpt = products.find(_.name == productName)
    (categoryOpt, productOpt) match {
      case (Some(category), Some(product)) if !category.products.exists(_.id == product.id) =>
        category.products += product
        Redirect(routes.CategoryController.showCategories(id))
      case _ =>
        BadRequest("Invalid category or product, or product already added to category")
    }
  }
}
