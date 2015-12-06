<?php
	$data =json_decode(file_get_contents('php://input'),true);

	$connectFilePath = "ConnectFile.txt";

	$myCarNumber = $data['MyNum'];
	$yourCarNumber = $data['YourNum'];

	$result = array();
	$result['data'] = array();


	//order이 connect 이면, my와 your를 연결하는 파일에 써준다.// 한번만
	$connectFile = fopen($connectFilePath, "a");
	$tempString = $myCarNumber." ".$yourCarNumber." \n";
	fwrite($connectFile, $tempString);
	fclose($connectFile);
	$result['result'] = true;
	

	echo json_encode($result,JSON_UNESCAPED_UNICODE);
	exit;
?>