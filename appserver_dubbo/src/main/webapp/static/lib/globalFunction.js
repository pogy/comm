function focusBlur(object,note,cls){    
	object.blur(function() {
        object.parent().removeClass(cls);
        object.removeClass('on');
        if ($.trim(object.val()) == "") {
        	object.val(note);
        }
    }).focus(function() {
        if ($.trim(object.val()) == note) {
        	object.val("")
            object.addClass('on');
        	object.parent().addClass(cls);
        }
    });
}




