package uk.co.mindbadger.footballresultsanalyser.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DivisionImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivision;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionId;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;
import uk.co.mindbadger.footballresultsanalyser.hibernate.HibernateUtil;

public class FootballResultsAnalyserHibernateDAO implements FootballResultsAnalyserDAO {

	private Map<Thread, Session> sessions = new HashMap<Thread, Session>();
	private DomainObjectFactory domainObjectFactory;

	@Override
	public void startSession() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();

		sessions.put(Thread.currentThread(), session);
	}

	@Override
	public void closeSession() {
		Session session = sessions.get(Thread.currentThread());
		session.close();

		sessions.remove(session);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Season> getSeasons() {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		List<Season> seasons = null;
		tx = session.beginTransaction();

		// Get the Seasons
		seasons = session.createQuery("from SeasonImpl").list();

		tx.commit();

		return seasons;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Division> getAllDivisions() {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		List<Division> divisions = null;
		tx = session.beginTransaction();

		// Get the Divisions
		divisions = session.createQuery("from Division").list();

		tx.commit();

		return divisions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Team> getAllTeams() {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		List<Team> teams = null;
		tx = session.beginTransaction();

		// Get the Divisions
		teams = session.createQuery("from Team").list();

		tx.commit();

		return teams;
	}

	@Override
	public Set<SeasonDivision> getDivisionsForSeason(int seasonNumber) {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		Season season = null;
		tx = session.beginTransaction();

		season = (Season) session.get(SeasonImpl.class, seasonNumber);

		// Get the Seasons
		// List seasonDivisions =
		// session.createQuery("from SeasonDivision SD where SD.season.ssnNum = "
		// + seasonNumber + " order by SD.divPos").list();
		// List seasonDivisions =
		// session.createQuery("from SeasonDivision SD").list();

		tx.commit();

		return season.getDivisionsInSeason();
	}

	@Override
	public Set<SeasonDivisionTeam> getTeamsForDivisionInSeason(int seasonNumber, int divisionId) {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		SeasonDivision seasonDivision = null;
		tx = session.beginTransaction();

		Season season = (Season) session.get(SeasonImpl.class, seasonNumber);
		Division division = (Division) session.get(DivisionImpl.class, divisionId);

		SeasonDivisionId seasonDivisionId = domainObjectFactory.createSeasonDivisionId(season, division);

		seasonDivision = (SeasonDivision) session.get(SeasonDivisionImpl.class, seasonDivisionId);

		tx.commit();

		return seasonDivision.getTeamsInSeasonDivision();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Fixture> getFixturesForTeamInDivisionInSeason(int seasonNumber, int divisionId, int teamId) {
		Transaction tx = null;

		Session session = sessions.get(Thread.currentThread());

		List<Fixture> fixtures = null;
		tx = session.beginTransaction();

		fixtures = session.createQuery("select F from FixtureImpl F join F.division D join F.season S" + " where S.seasonNumber = " + seasonNumber + " and D.divisionId = " + divisionId + " order by F.fixtureDate").list();

		tx.commit();

		return fixtures;
	}

	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}

	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}
}
