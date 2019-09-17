  function setMark(evaluateId, val){

   //Делаем стиль всех 5 кнопок категории, которой выставлена оценка, обычным
   var elements = document.getElementsByClassName(this.event.srcElement.className);
    for(var i=0; i<elements.length; i++) {
            elements[i].style.background = "#4CAF50";
        }
   //Выделяем нажатую кнопку
    var element = this.event.srcElement;
     element.style.background = "#FFAAAA";

    //Устанавливаем оценку по выбранному критерю в скрытое поле
    var eval = document.getElementById(evaluateId);
    eval.value=val;

    //Обновление общей оценки
    var elements = document.getElementsByClassName("mark");
    var summary = document.getElementById("summaryValue");
    summary.innerHTML="0";
        for(var i=0; i<elements.length; i++) {
         summary.innerHTML= parseInt(summary.innerHTML) + parseInt(elements[i].value);
        }
        document.getElementById("buttonSummaryValue").innerHTML=summary.innerHTML;
    }

$(document).ready(function() {
    alert("1111111");
    var errorMessage = /*[[${errorMessage}]]*/ 'Sebastian';
    if(errorMessage!=null){
        document.getElementById("buttonSummaryValue").disabled=true;
    }
    online();
});
    function online(){
	    setInterval(()=> isPerformanceNew(), 10000)
	}


     function isPerformanceNew(){
     var performanceId = "${performance.performanceId}";
		 /*<![CDATA[*/
        var performanceId = /*[[${performance.performanceId}]]*/ 'Sebastian';
          /*]]>*/


		$.ajax({
			type: 'POST',
			url: "/evaluation/isPerformanceNew",
			data: ({performanceId:performanceId}),
			success: function(data){
		    console.log(data);

		        if(data=="true"){
        		window.location.href = "/evaluation";
        	    }
        	}
		});
     }

//Проверка на заполненность оценок перед отправкой формы
jQuery("#evaluateWrapper").submit(function(e) {
         var elements = document.getElementsByClassName("mark");
    var count = 0;
    for(var i=0; i<elements.length; i++) {
            var tcell = elements[i].parentNode;
            tcell.style.background = "#AFCDE7";
            if(elements[i].value == "0"){
              tcell.style.background="#FF0000";
              count+=1;
            }
        }
if(count>0){
return false;
}else{
document.getElementById("buttonSummaryValue").disabled=true;
}

});

