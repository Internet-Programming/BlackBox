<?php
require (__DIR__.'/DBConnect.php');
$rtn = array();
$rtn['result'] = true;
$rtn['data'] = $_POST;

$carNumber = $rtn['data']['Num'];
$password = $rtn['data']['PW'];

//print_r($password."  23455");

$DBconn = connectDB();

insertInfoData ($carNumber, $password);

closeDB($DBconn);


echo json_encode($rtn);

exit;
?>