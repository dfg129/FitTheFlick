package com.mobileomega.controllers


//import com.mobileomega.models.Image
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.modules.reactivemongo._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}


import reactivemongo.api.gridfs._
//import reactivemongo.api.gridfs.Implicits._

// Reactive Mongo imports
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

  def getImageFile(id: String) = Action {
    Async {
      // find the matching image, if any, and streams it to the client
      println("getImage : " + id)
      val docId = BSONDocument("_id" -> new BSONObjectID(id))
      val doc = null //gridFS.find(docId)
      serve(gridFS, doc)
    }
  }

  def index = Action { implicit request =>
  	Async {
  		implicit val reader = com.mobileomega.models.ImageHandlers.ImageBSONReader

  		val query = BSONDocument("$query" -> BSONDocument())
  		System.out.println("### " + query)
  		val found = collection.find(query) 
  		found.toList.map(images => Ok(views.html.index(images)))
  	}

  }
  
}