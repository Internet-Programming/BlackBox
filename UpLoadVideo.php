<?php
	require (__DIR__.'/DBConnect.php');
	
	$rtn = array();
	$rtn['result'] = true;
	$rtn['data'] = array();

	$file_path = "videos/";

	$file_path = $file_path . basename ( $_FILES['uploaded_file']['name']);
	if (move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
		$rtn['data']['success'] = true;
	} else {
		$rtn['data']['success'] = false;
	}

	ehco json_encode($rtn,JSON_UNESCAPED_UNICODE);
	exit;
?>