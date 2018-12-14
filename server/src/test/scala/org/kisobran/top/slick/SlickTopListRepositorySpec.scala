package org.kisobran.top.slick

import java.nio.file.Files

import org.kisobran.top.model.Entry
import org.scalatest.FlatSpec
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import slick.jdbc.{DriverDataSource, H2Profile}

import scala.concurrent.ExecutionContext

object DbTestConfiguration {

  def testMySQL = {
    val tempDir = Files.createTempDirectory(null)

    val ds = new DriverDataSource(
      url = s"jdbc:h2:${tempDir.toFile.getAbsolutePath}/test.db;DB_CLOSE_DELAY=-1;MODE=MySQL;",
      driverClassName = "org.h2.Driver")

    ds
  }

}

class SlickTopListRepositorySpecextends extends FlatSpec
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with ScalaFutures
  with IntegrationPatience {

  val slickTopListRepository = new SlickTopListRepository(DbTestConfiguration.testMySQL)(H2Profile, ExecutionContext.global)

  override def beforeEach(): Unit = {
    slickTopListRepository.ensureTablesPresent(true)
  }

  "slickTopListRepository" should "play nice" in {
    val stored = slickTopListRepository.createTopList(
      "use@somebody.com",
      (1 to 10).map { i => Entry(s"artist${i}", s"song${i}") },
      "best-list"
    ).futureValue

    val returned = slickTopListRepository.getTopList(stored.get.id).futureValue

    returned.get.title should be("best-list")
    returned.get.userEmail should be("use@somebody.com")
    returned.get.song1 should be("song1")
    returned.get.song2 should be("song2")
    returned.get.song3 should be("song3")
    returned.get.song4 should be("song4")
    returned.get.song5 should be("song5")
    returned.get.song6 should be("song6")
    returned.get.song7 should be("song7")
    returned.get.song8 should be("song8")
    returned.get.song9 should be("song9")
    returned.get.song10 should be("song10")

    returned.get.artist1 should be("artist1")
    returned.get.artist2 should be("artist2")
    returned.get.artist3 should be("artist3")
    returned.get.artist4 should be("artist4")
    returned.get.artist5 should be("artist5")
    returned.get.artist6 should be("artist6")
    returned.get.artist7 should be("artist7")
    returned.get.artist8 should be("artist8")
    returned.get.artist9 should be("artist9")
    returned.get.artist10 should be("artist10")

  }

}
