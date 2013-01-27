package com.mobileomega.controllers


import play.api._
import play.api.mvc._
import play.api.Play.current
import play.modules.reactivemongo._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}


import reactivemongo.api.gridfs._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler


// Reactive Mongo plugin
import play.modules.reactivemongo._



object Application extends Controller  with MongoController {
  val db = ReactiveMongoPlugin.db
  val collection = db("images")

  val gridFS = new GridFS(db, "fs")
  gridFS.ensureIndex()

  def getImageFile(name: String) = Action {
    Async {
      import reactivemongo.api.gridfs.Implicits.DefaultReadFileReader
      Logger.info("here")
      val doc = BSONDocument("filename" -> BSONString(name))
      val img = gridFS.find(doc)
      serve(gridFS, img)
    }
  }

  def index = Action { implicit request =>
  	Async {
  		implicit val reader = com.mobileomega.models.ImageHandlers.ImageBSONReader

      Logger.info("start")
  		val query = BSONDocument("$query" -> BSONDocument())
  		val found = collection.find(query) 
  		found.toList.map(images => Ok(views.html.index(images)))
  	}

  }
  
}