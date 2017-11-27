<?php

$target_dir = "uploads/";
$target_file = $target_dir . basename($_FILES["fileToUpload"]["name"]);
$uploadOk = 1;
$FileType = pathinfo($target_file,PATHINFO_EXTENSION);
// Existe el archivo
if (file_exists($target_file)) {
 echo "Lo siento, el archivo ya existe en el servidor.";
 $uploadOk = 0;
}
// Tamaño maximo = 1 MB
else if ($_FILES["fileToUpload"]["size"] > 1000000) {
 echo "Lo siento, tu archivo es demasiado grande";
 $uploadOk = 0;
}

// Archivos Permitidos = JPG, PNG, TXT, PDF, HTML, MP3
else if($FileType != "jpg" && $FileType != "png" && $FileType != "txt" && $FileType != "pdf" && $FileType != "html" && $FileType != "pdf" ) {
 echo "Lo siento, solo archivos JPG, PNG, TXT, HTML, PDF, MP3 son permitidos.";
 $uploadOk = 0;
}

if ($uploadOk == 0) {
 echo "\nTu archivo no se ha subido.";

} else {
 if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
 echo "El archivo ". basename( $_FILES["fileToUpload"]["name"]). " se ha subido con exito.";
 } else {
 echo "Lo siento, ha ocurrido un error subiendo tu archivo.";
 }
}
?>
