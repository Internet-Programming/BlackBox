<?php
	require (__DIR__.'/DBConnect.php');

	$rtn = array();
	$rtn['result'] = true;
	$rtn['data'] = array();

	$file_path = "videos/";

	$DBconn = connectDB();


	$carNumber = getallheaders()['Carnumber'];
	/* 헤더 부분 데이터 확인하는 부분
	print_r(getallheaders());
	foreach (getallheaders() as $key => $value) {
		print_r($key);
		if ($key == "Carnumber") $carNumber = $value;
	}
	*/
	$file_path = $file_path . basename ( $_FILES['uploaded_file']['name']);
	if (move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {


		insertVideoUri ($DBconn, $carNumber, $file_path);

		$rtn['data']['success'] = true;
	} else {
		$rtn['data']['success'] = false;
	}
	closeDB($DBconn);


	echo json_encode($rtn,JSON_UNESCAPED_UNICODE);
	exit;
?>