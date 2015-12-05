<?php
header('Content-Type: text/html; charset=UTF-8');
require (__DIR__.'/DBConnect.php');
$rtn = array();
$rtn['data'] =json_decode(file_get_contents('php://input'),true);

$carNumber = $rtn['data']['Num'];


$DBconn = connectDB();

$result = selectInfoDataFront ($DBconn, $carNumber);



$i = 0;
while($data = mysqli_fetch_array($result)) {
	$i++;
}

if ($i > 0) {
	$rtn['result'] = false;
} else {
	$rtn['result'] = true;
}


closeDB($DBconn);


echo json_encode($rtn,JSON_UNESCAPED_UNICODE);

exit;
?>