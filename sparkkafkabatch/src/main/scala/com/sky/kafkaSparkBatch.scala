package com.sky

// Spark
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.json_tuple
import org.apache.spark.sql.functions._

// Avro
import com.databricks.spark.avro._





object kafkaSparkBatch {

  def main(args: Array[String]): Unit = {

    println( "SparkJob Start......" )

    // SparkSession Creation
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("kafkaSparkBatch")
      .getOrCreate()

    println("Spark Session Initialized" )

    val df = spark
        .read
        .format("kafka")
        .option("kafka.bootstrap.servers", "10.170.14.18:9092")
        //.option("subscribe", "gb.nowtv.ac.dcm.stream_stop")
         .option("subscribe", "de.skyq.search.feedback")
        .load()


    println("Kafka Topic Pointed" )


    df.describe()

    // get Kafka Events Schema
    df.printSchema()

    // Get min and max Offsets by Partition
    df.select(  df("offset").cast("long"),
                df("partition").cast("integer")
    ).groupBy("partition").agg(min("offset"), max("offset")).show()

/*
    // Query Kafka Messages
    df.select(
      json_tuple(
          df("value").cast("string")
         ,"ipAddress", "videoId", "sessionId", "deviceId", "userId"
      )
    ).show(10, false)
*/





    df.select("value").show(10, false)

    println( "---------------------------------------------------------------------------" )
    println( "---------------------------------------------------------------------------" )

    println( "...Stop Spark Session..." )

    spark.stop()

    println( "......SparkJob End." )

  }
}
