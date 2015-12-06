<?php
	$data =json_decode(file_get_contents('php://input'),true);

	$connectFilePath = "ConnectFile.txt";

	$myCarNumber = $data['MyNum'];
	$yourCarNumber = $data['YourNum'];

	$result = array();
	$result['data'] = array();


	//한쪽에서 연결을 끊으려 할때 오는 정보 처리 connect파일에서 정보를 지운다. //한번만
	//$connectFile = fopen($connectFilePath, "r+");
	//$tempString="";

	$lines = file($connectFilePath, FILE_IGNORE_NEW_LINES);
	$remove = $myCarNumber;
	foreach($lines as $key => $line) {
	  if(stristr($line, $remove)) unset($lines[$key]); //$remove 가 들어있는 한줄 전체를 없앤다.
	}

	$data = implode('\n', array_values($lines));
	$file = fopen($connectFilePath,"w");
	fwrite($file, $data);
	fclose($file);
		/*
	while (($tempString = fgets($connectFile))>0) {
		$arrString = explode(" ", $tempString);
		if (!strcmp($arrString[0],$myCarNumber) && !strcmp($arrString[1], $yourCarNumber)) {
			//mb_strlen($tempString, "EUC_KR");
			//mb_strlen($tempString, "UTF-8");
			fseek($connectFile, mb_strlen($tempString, "EUC_KR") * (-1) , SEEK_CUR);

			fwrite($connectFile, "\n",);

			
		} else if (!strcmp($arrString[1],$myCarNumber) && !strcmp($arrString[0], $yourCarNumber)) {

		}
	}
	*/

		////여기해야댐

	//fclose($connectFile);
	$result['result'] = true;
	

	echo json_encode($result,JSON_UNESCAPED_UNICODE);
	exit;
?>