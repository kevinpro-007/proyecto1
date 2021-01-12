
$(document).ready(function mostrarFecha(){
	var dt=new Date();
		var mes=new Array()
		mes[1]='Enero';
		mes[2]='Febrero';
		mes[3]='Marzo';
		mes[4]='Abril';
		mes[5]='Mayo';
		mes[6]='Junio';
		mes[7]='Julio';
		mes[8]='Agosto';
		mes[9]='Septiembre';
		mes[10]='Octubre';
		mes[11]='Nobiembre';
		mes[12]='Diciembre';
	var n=mes[dt.getMonth()+1];
	var day=dt.getDate();
	var year=dt.getFullYear();

	var fecha=day+"/"+n+"/"+year;
	$("#fecha").val(fecha);
	
	$('#misroles').on('change',function(){
		var dato=$(this).val();
		console.log('dato es',dato);
	});
});
	


