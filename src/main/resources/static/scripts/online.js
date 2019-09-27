//При загурузке страницы
$(document).ready( function () {
    // Окрашивание ячеек, которые ужюе заполнены оценками
    coloringCells();
    //Запуск опроса сервера на наличие новых оценок активного performance
    getNewMarksForActivePerformance();   //единоразовый опрос сервера
    online(); //запустить переодический опрос сервера

 });



//Отправка на сервер активного id Performance
function send(ids){
    //Убирает обводку бывшей активной строки
    var elements = document.getElementsByClassName("selected");
    for(var i=0; i<elements.length; i++) {
        elements[i].className="";
    }
     // Установлевает обводку строки нового активного элемента
    document.getElementById(ids).className="selected";
    //Отправка запроса на сервер - установка нового активного Performance
    $.ajax({
        type: 'POST',
        url:'/online/send',
        data:({performanceID:ids}),
        success: function(msg){
        //Успешный response
        }
    });
}

//Запуск переодического опроса сервера на новые оценки активного Performance
function online(){
    setInterval(()=> getNewMarksForActivePerformance(), 10000)
}

//Опрос сервера на новые оценки активного Performance
function getNewMarksForActivePerformance(){
    $.ajax({
        type: 'POST',
        url: "/online/getNewMarksForActivePerformance",
		//	 data: {field1 : "hello", field2 : "hello2"},
        success: function(data){
            console.log(data);
            var obj = jQuery.parseJSON(data);
            $.each(obj, function(key,value) {
                if(key=='activePerformanceId'){
                    var elem = document.getElementById(value);
                    elem.className="selected";
                }else{
                    if(value!=0){
                        var elem = document.getElementById(key);
                        elem.innerHTML=value;
                        elem.style.backgroundColor = '#8A2BE2';
                        elem.style.color = '#FFFFFF';
                    }
                }
            });
        }
    });
}

//При загрузке страницы обновляем цвет ячеек (если заполнены, то выделяем)
function coloringCells(){
     var elements = document.getElementsByClassName("marks");
        for(var i=0; i<elements.length; i++) {
            var elem = elements[i];
            if(elem.innerHTML!="0"){
            elem.style.backgroundColor = '#8A2BE2';
            elem.style.color = '#FFFFFF';
            }
        }
}