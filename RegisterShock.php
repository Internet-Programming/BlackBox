<?php
	$data =json_decode(file_get_contents('php://input'),true);

	$sensorFilePath = "SensorFile.txt";

	$myCarNumber = $data['MyNum'];
	$yourCarNumber = $data['YourNum'];

	$result = array();
	$result['data'] = array();


	//차가 충격을 받았을경우 sensorFile에 상대편 차를 쓴다. //한번만
	$sensorFile = fopen($sensorFilePath, "a");
	$tempString = $yourCarNumber." \n";
	fwrite($sensorFile, $tempString);
	fclose($sensorFile);
	$result['result'] = true;

	echo json_encode($result,JSON_UNESCAPED_UNICODE);
	exit;
?>