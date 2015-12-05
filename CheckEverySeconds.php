<?php
	$data =json_decode(file_get_contents('php://input'),true);

	$connectFilePath = "ConnectFile.txt";
	$sensorFilePath = "SensorFile.txt";

	$myCarNumber = $data['MyNum'];
	$yourCarNumber = $data['YourNum'];
	$order = $data['Order'];

	$result = array();
	$result['data'] = array();
	



	if (!strcmp($order, "solo")) { //order이 solo와 일치하면, 아직 연결이 안되었다는 듯이다. 따라서 connect파일을 계속 확인해 내 차가 연결된 목록이 있는지 확인한다.
		$connectFile = fopen($connectFilePath, "r");
		$tempString="";
		$hasMyNum = false;
		while (($tempString = fgets($connectFile[, 999]))>0) {
			
			$arrString = split(" ", $tempString);
			if (!strcmp($arrString[0],$myCarNumber)) {
				$hasMyNum = true;
				$result['data']['MyNum'] = $arrString[0];
				$result['data']['YourNum'] = $arrString[1];
			} else if (!strcmp($arrString[1],$myCarNumber)) {
				$hasMyNum = true;
				$result['data']['MyNum'] = $arrString[1];
				$result['data']['YourNum'] = $arrString[0];
			}
				
		}
		$result['result'] = $hasMyNum;
		fclose($connectFile);
	} else if (!strcmp($order, "connect")) { //order이 connect 이면, my와 your를 연결하는 파일에 써준다.
		$connectFile = fopen($connectFilePath, "a");
		$tempString = $myCarNumber." ".$yourCarNumber." \n";
		fwrite($connectFile, $tempString[,999]);
		fclose($connectFile);
		$result['result'] = true;
	} else if (!strcmp($order, "checkShock")) { //3초마다 내 차가  충격을 받앗는지 체크 sensorFile에서 가져온다. 또한,3초마다 혹시 상대가 연결을 끊었는지 connect파일에서 확인한다.

	} else if (!strcmp($order, "shock")) { //차가 충격을 받았을경우 sensorFile에 상대편 차를 쓴다.
		$sensorFile = fopen($sensorFilePath, "a");
		$tempString = $yourCarNumber." \n";
		fwrite($sensorFile, $tempString[,999]);
		fclose($sensorFile);
		$result['result'] = true;
	} else if (!strcmp($order, "disconnect")) { //한쪽에서 연결을 끊으려 할때 오는 정보 처리 connect파일에서 정보를 지운다.
		$connectFile = fopen($connectFilePath, "r+");
		$tempString="";


		////여기해야댐
		

	} else {
		$result['result'] = false;
	}

	echo json_encode($result,JSON_UNESCAPED_UNICODE);
	exit;

?>