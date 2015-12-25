//焦点图切换
var mySwiperfocus = new Swiper('#m-focus', {
	pagination: '#m-pagination',
	loop: true,
	grabCursor: true,
	paginationClickable: true,
	spaceBetween: 30,
	centeredSlides: true,
	autoplay: 4000,
	calculateHeight: true
});
var swiper = new Swiper('#t-focus', {
    paginationClickable: '.swiper-pagination',
    nextButton: '.swiper-button-next',
    prevButton: '.swiper-button-prev',
    spaceBetween: 30,
    hashnav: true
});
var swiper = new Swiper('#p-focus', {
    pagination: '#p-pagination',
    paginationClickable: '.swiper-pagination',
    nextButton: '.swiper-button-next',
    prevButton: '.swiper-button-prev',
    spaceBetween: 30,
    hashnav: true,
    paginationBulletRender: function (index, className) {
        return '<span class="' + className + '">' + (index + 1) + '</span>';
    }
});
(function(){
	$('.p-num .numAll').html($('#p-focus').find('.swiper-slide').length);
})();
var canvas = document.getElementById("canvas");
function touchStart(event) {closeLeft();}
canvas.addEventListener("touchstart", touchStart, false);