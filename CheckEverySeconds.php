<?php
	$data =json_decode(file_get_contents('php://input'),true);

	$connectFilePath = "ConnectFile.txt";
	$sensorFilePath = "SensorFile.txt";

	$myCarNumber = $data['MyNum'];
	$yourCarNumber = $data['YourNum'];
	$order = $data['Order'];

	$result = array();
	$result['data'] = array();
	



	if (!strcmp($order, "solo")) { //order이 solo와 일치하면, 아직 연결이 안되었다는 듯이다. 따라서 connect파일을 계속 확인해 내 차가 연결된 목록이 있는지 확인한다. 3초마다
		$connectFile = fopen($connectFilePath, "r");
		$tempString="";
		$hasMyNum = false;
		while (($tempString = fgets($connectFile))>0) {
			
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
	} else if (!strcmp($order, "checkShock")) { //3초마다 내 차가  충격을 받앗는지 체크 sensorFile에서 가져온다. 또한,3초마다 혹시 상대가 연결을 끊었는지 connect파일에서 확인한다.
		//3초마다
		$lines = file($sensorFilePath, FILE_IGNORE_NEW_LINES);
		$remove = $myCarNumber;
		$result['data']['shock'] = false;
		foreach($lines as $key => $line) {
			if(stristr($line, $remove)) {
		  		unset($lines[$key]); //$remove 가 들어있는 한줄 전체를 없앤다.
		  		$result['data']['shock'] = true;
			}
		}

		$data = implode('\n', array_values($lines));
		$file = fopen($path);
		fwrite($file, $data);
		fclose($file);


		$result['data']['connect'] = false;
		$lines = file($connectFilePath, FILE_IGNORE_NEW_LINES);
		$remove = $myCarNumber;
		foreach($lines as $key => $line) {
			if(stristr($line, $remove)) {
		  		$result['data']['shock'] = true;
			}
		}		

		$result['result'] = true;
		

	} else {
		$result['result'] = false;
	}

	echo json_encode($result,JSON_UNESCAPED_UNICODE);
	exit;

?>