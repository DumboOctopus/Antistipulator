import kareltherobot.*;
import java.awt.Color;
import java.util.*;
import kareltester.*;
/**
 * Write a description of class ABCBot here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ABCBot extends Robot implements TestableKarel
{
    public ABCBot(int st, int ave, Direction dir, int num)
    {
        super(st, ave, dir, num);
    }
    public void turnRight()
    {
        turnLeft();turnLeft();turnLeft();
    }
    
   
    
    public void a () {
    	move();
    	move();
    	move();
    }
    public void b () {
    	move();
    	turnLeft();
    	turnLeft();
    	turnLeft();
    }
    public void c () {
    	move();
    	turnLeft();
    	move();
    }
    public void d () {
    	turnRight();
    	turnRight();
    	turnRight();
    }
    public void e () {
    	a();
    	turnLeft();
    	turnLeft();
    	turnLeft();
    }
    public void f () {
    	move();
    	turnRight();
    }
    public void g () {
    	move();
    	turnLeft();
    	a();
    }
    public void h () {
    	move();
    	pickBeeper();
    	turnLeft();
    	turnLeft();
    }
    public void i () {
    	a();
    	move();
    }
    public void j () {
    	move();
    	d();
    }
    public void k () {
    	a();
    	turnLeft();
    }
    public void l () {
    	a();
    	a();
    }
    public void m () {
    	move();
    	turnLeft();
    	b();
    	f();
    	c();
    	d();
    	b();
    	f();
    	c();
    }
    public void n () {
    	move();
    	c();
    }
    public void o () {
    	turnLeft();
    	turnRight();
    	turnRight();
    }
    public void p () {
    	c();
    	turnRight();
    }
    public void q () {
    	turnLeft();
    	turnLeft();
    }
    public void r () {
    	b();
    	j();
    }
    public void s () {
    	a();
    	i();
    }
    public void t () {
    	f();
    	k();
    }
    public void u () {
    	move();
    	f();
    }
    public void v () {
    	f();
    	a();
    }
    public void w () {
    	turnRight();
    	b();
    }
    public void x () {
    	move();
    	turnLeft();
    }
    public void y () {
    	move();
    	b();
    }
    public void z () {
    	a();
    	d();
    }
    public void now () {
    	c();
    	b();
    	l();
    }
    public void I_Know () {
    	f();
    	n();
    }
    public void my () {
    	g();
    	b();
    }
    public void abcs () {
    	h();
    	i();
    }
    public void next () {
    	c();
    	turnLeft();
    }
    public void time () {
    	pickBeeper();
    	q();
    }
    public void wont () {
    	move();
    	j();
    }
    public void u_sing () {
    	move();
    	o();
    }
    public void with () {
    	x();
    	e();
    }
    public void moi () {
    	p();
    	l();
    }
    public void task () {
        System.out.println("I was called :D");
    	turnRight();
    	turnLeft();
    	n();
    	f();
    	g();
    	t();
    	k();
    	turnLeft();
    	f();
    	i();
    	my();
    	b();
    	i();
    	t();
    	b();
    	move();
    	abcs();
    	b();
    	i();
    	abcs();
    	with();
    	t();
    	l();
    	next();
    	f();
    	g();
    	o();
    	i();
    	g();
    	i();
    	j();
    	b();
    	my();
    	c();
    	h();
    	u();
    	my();
    	moi();
    	i();
    	c();
    	g();
    	turnLeft();
    	c();
    	h();
    	y();
    	b();
    	a();
    	turnRight();
    	move();
    	move();
    	q();
    	a();
    	r();
    	e();
    	u();
    	a();
    	time();
    	z();
    	u();
    	q();
    	a();
    	turnRight();
    	my();
    	s();
    	b();
    	s();
    	d();
    	n();
    	turnRight();
    	j();
    	i();
    	abcs();
    	f();
    	now();
    	i();
    	c();
    	g();
    	k();
    	g();
    	move();
    	abcs();
    	f();
    	v();
    	next();
    	u();
    	i();
    	o();
    	j();
    	p();
    	wont();
    	e();
    	k();
    	turnLeft();
    	z();
    	e();
    	wont();
    	u_sing();
    	u_sing();
    	g();
    	j();
    	z();
    	n();
    	f();
    	u_sing();
    	g();
    	turnLeft();
    	I_Know();
    	w();
    	p();
    	y();
    	g();
    	turnLeft();
    	move();
    	pickBeeper();
    	turnLeft();
    	n();
    	w();
    	f();
    	q();
    	n();
    	turnRight();
    	c();
    	d();
    	b();
    	n();
    	turnRight();
    	k();
    	y();
    	l();
    	turnRight();
    	i();
    	q();
    	a();
    	c();
    	c();
    	b();
    	c();
    	v();
    	f();
    	next();
    	b();
    	n();
    	j();
    	e();
    	g();
    	e();
    	f();
    	i();
    	now();
    	g();
    	l();
    	a();
    	time();
    	l();
    	a();
    	e();
    	l();
    	b();
    	u();
    	wont();
    	x();
    	d();
    	u_sing();
    	wont();
    	g();
    	k();
    	m();
    	turnLeft();
    	f();
    	r();
    	m();
    	d();
    	h();
    	f();
    	r();
    	p();
    	r();
    	p();
    	r();
    	m();
    	d();
    	b();
    	b();
    	a();
    	e();
    	I_Know();
    	turnLeft();
    	l();
    	w();
    	i();
    	g();
    	w();
    	k();
    	g();
    	turnRight();
    	next();
    	e();
    	t();
    	h();
    	t();
    	g();
    	turnRight();
    	r();
    	e();
    	b();
    	z();
    	with();
    	e();
    	u();
    	f();
    	j();
    	c();
    	j();
    	z();
    	y();
    	I_Know();
    	turnLeft();
    	e();
    	b();
    	k();
    	f();
    	q();
    	e();
    	t();
    	j();
    	i();
    	d();
    	a();
    	h();
    	s();
    	b();
    	t();
    	j();
    	a();
    	p();
    	a();
    	r();
    	a();
    	turnRight();
    	v();
    	r();
    	moi();
    	pickBeeper();
    	turnLeft();
    	j();
    	s();
    	b();
    	f();
    	l();
    	a();
    	k();
    	v();
    	c();
    	d();
    	i();
    	moi();
    	w();
    	now();
    	a();
    	w();
    	s();
    	h();
    	s();
    	c();
    	g();
    	l();
    	c();
    	I_Know();
    	f();
    	b();
    	k();
    	j();
    	e();
    	n();
    	b();
    	v();
    	turnRight();
    	n();
    	turnLeft();
    	b();
    	p();
    	x();
    	b();
    	h();
    	turnLeft();
    	l();
    	b();
    	move();
    	h();
    	u();
    	l();
    	l();
    	time();
    	turnLeft();
    	k();
    	with();
    	s();
    	h();
    	s();
    	g();
    	k();
    	c();
    	v();
    	time();
    	e();
    	e();
    	e();
    	I_Know();
    	turnLeft();
    	k();
    	y();
    	a();
    }

}
