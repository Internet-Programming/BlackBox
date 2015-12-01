<?php 

require (__DIR__.'/DBConnect.php');
$data =json_decode(file_get_contents('php://input'),true);

$carNumber = $data['Num'];



?>