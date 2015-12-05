<?php
//$connect;


function connectDB () {

	$hostname = "mysql.hostinger.kr";
	$username = "u762849870_min";
	$password = "2012920020";
	$dbname = "u762849870_min";
	//Connect DB &
	$connect = mysqli_connect($hostname,$username,$password,$dbname) or die("DB접속에러");
	//$connect = mysql_connect('localhost','root','mks295') or die("DB접속에러");

	//Select DB
	//$result = mysql_select_db($dbname, $connect) or die("DB선택에러");

	/*
	if(!$result) 
		echo("MySQL Server Connect 실패");
	}
	*/
	//Print Query Result
	//$result = @mysql_query($strQuery) or die("SQL error");

	return $connect;
}

function closeDB ($connect) {
	mysqli_close($connect);
}

function insertInfoData ($connect, $carNumber, $password) {
	mysqli_query($connect, "INSERT INTO clientInfo VALUES('".$carNumber."','".$password."')");
}

function selectAllDataTable ($connect, $table) {
	return mysqli_query($connect, "select * from ".$table);
}

function selectInfoDataSignUp ($connect, $carNumber, $password) {
	//print "select * from clientInfo WHERE CarNumber = '". $carNumber. "' AND password = '". $password."'";
	//return mysqli_query("select * from clientInfo WHERE CarNumber = '". $carNumber. "' AND password = '". $password."';");
	return mysqli_query($connect, "select * from clientInfo WHERE CarNumber = '". $carNumber. "';");
}

function selectInfoDataSignIn ($connect, $carNumber, $password) {
	//print "select * from clientInfo WHERE CarNumber = '". $carNumber. "' AND password = '". $password."'";
	return mysqli_query($connect, "select * from clientInfo WHERE CarNumber = '". $carNumber. "' AND password = '". $password."';");
}

function selectInfoDataFront ($connect, $carNumber) {
	return mysqli_query($connect, "select * from clientInfo WHERE CarNumber = '". $carNumber. "';");
}

function insertVideoUri ($connect, $carNumber, $uri) {
	mysqli_query($connect, "INSERT INTO VideoList (CarNumber, URI) VALUES('".$carNumber."','".$uri."')");
}

function selectVideoByCarName ($connect, $carNumber) {
	return mysqli_query($connect, "select URI from VideoList WHERE CarNumber = '". $carNumber."';");
}
/*
connectDB();

closeDB();
*/
?>