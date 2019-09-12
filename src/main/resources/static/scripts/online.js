//При загурузке страницы
$(document).ready( function () {
    //Запуск опроса сервера на наличие новых оценок активного performance
    getNewMarksForActivePerformance();   //единоразовый опрос сервера
    online(); //запустить переодический опрос сервера
        //Сортировка списка
    var sort = [];
    $('.sortable-ul li').each(function(){
        sort.push($(this).data('id'));
    });
    $.cookie('sort', JSON.stringify(sort));
        //Сохранить сортировку из cookie в DB при нажатии кнопки сохранить
    $("#submitBtn").click(function(){
        var lock = document.getElementById('skm_LockPane');
        lock.className = 'LockOn';
        var formSentCount = $.cookie('sort');
        var element = document.getElementById("json");
        element.value = formSentCount;
        $("#myForm").submit($.cookie('sort')); // Submit the form
    });
 });

//Реализация сортировки
$( function() {
    $('.sortable-ul, .sortable-ul li > ul').sortable({
        stop: function(event, ui) {
        // Собираем все data-id в массив.
            var sort = [];
            $('.sortable-ul li').each(function(){
                sort.push($(this).data('id'));
            });
		    // И сохраняем его в cookie в виде строки JSON.
        $.cookie('sort', JSON.stringify(sort));
		     //  alert($.cookie('sort'));
	    }
    });
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