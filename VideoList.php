<?php 

	require (__DIR__.'/DBConnect.php');
	$data =json_decode(file_get_contents('php://input'),true);

	$carNumber = $data['Num'];

	$DBconn = connectDB();

	$result = selectVideoByCarName($DBconn, $carNumber);

	closeDB($DBconn);

	//print_r($result);


	

	$rtn = array();
	$rtn['result'] = true;
	$rtn['data'] = array();
	$rtn['data']['filelist'] = array();

	$i = 0;
	while($data = mysqli_fetch_array($result)) {
		

		$rtn['data']['filelist'][$i]['URI'] = $data[0];
		$rtn['data']['filelist'][$i]['filename'] = substr($data[0], 7);
		$i++;
	}


	if ($i > 0) {
		$rtn['data']['success'] = true;
	} else {
		$rtn['data']['success'] = false;
	} 


	echo json_encode($rtn,JSON_UNESCAPED_UNICODE);
	exit;

?>