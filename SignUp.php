<?php
header('Content-Type: text/html; charset=UTF-8');
require (__DIR__.'/DBConnect.php');
$rtn = array();
$rtn['data'] =json_decode(file_get_contents('php://input'),true);

$carNumber = $rtn['data']['Num'];
$password = $rtn['data']['PW'];


//print_r($password."  23455");

$DBconn = connectDB();

$result = selectInfoDataSignUp ($DBconn, $carNumber, $password);



$i = 0;
while($data = mysqli_fetch_array($result)) {
	$i++;
}

if ($i > 0) {
	$rtn['result'] = false;
} else {
	insertInfoData ($DBconn, $carNumber, $password);

	$rtn['result'] = true;
}


closeDB($DBconn);


echo json_encode($rtn,JSON_UNESCAPED_UNICODE);

exit;
?>