<?php

$user_name=$_POST["user_name"];
$user_password=$_POST["password"];
$name=$_POST["name"];
$email=$_POST["email"];

require 'init.php';

if($con){
    $sql="SELECT * FROM user_info WHERE user_name='$user_name'";
    $result=mysqli_query($con,$sql);

    if(mysqli_num_rows($result)>0){
        $status="ok";
        $result_code=0;
        echo json_encode(array('status'=>$status,'result_code'=>$result_code));
    }else{
        $sql="INSERT INTO user_info (name,user_name,password,email) VALUES('$name','$user_name','$user_password','$email')";
        if(mysqli_query($con,$sql)){
            $status="ok";
            $result_code=1;
            echo json_encode(array('status'=>$status,'result_code'=>$result_code));
        }else{
            $status="failed";
            echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
        }
    }
}else{
    $status="failed";
    echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
}
mysqli_close($con);
?>